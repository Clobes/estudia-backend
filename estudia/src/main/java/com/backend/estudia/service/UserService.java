package com.backend.estudia.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.backend.estudia.dto.NotificationDTO;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backend.estudia.converter.UserConverter;
import com.backend.estudia.dto.UpdatePasswordDTO;
import com.backend.estudia.dto.UserLoggedDTO;
import com.backend.estudia.dto.UserLoginDTO;
import com.backend.estudia.entity.Certificate;
import com.backend.estudia.entity.Course;
import com.backend.estudia.entity.Inscription;
import com.backend.estudia.entity.Role;
import com.backend.estudia.entity.User;
import com.backend.estudia.exception.GeneralServiceException;
import com.backend.estudia.exception.ValidateServiceException;
import com.backend.estudia.exception.NoDataFoundException;
import com.backend.estudia.repository.ICertificateRepo;
import com.backend.estudia.repository.ICourseRepository;
import com.backend.estudia.repository.IRoleRepository;
import com.backend.estudia.repository.IUserRepository;
import com.backend.estudia.util.Kte;
import com.backend.estudia.util.Roles;
import com.backend.estudia.util.SendMail;
import com.backend.estudia.validator.UserValidator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	private IRoleRepository roleRepo;
	
	@Autowired
	private IUserRepository userRepo;
	
	@Autowired
	private ICourseRepository courseRepo;
	
	@Autowired
	private ICertificateRepo certificateRepo;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private UserConverter userConverter;
	
	@Value("{jwt.secretkey}")
	private String secretKey;
	
	@Value("${config.dateFormat}")
	private String dateFormat;
	
	@Value("${config.path.static.file}")
	private String UPLOAD_FOLDER;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public Page<User> getUsersWithFilters(Pageable pageable, Long id, String ciFilter, String roleFilter,
			String nameFilter, String emailFilter, String globalFilter, String orderBy) {
		try {
			
			return userFilters(pageable, id, ciFilter, roleFilter, nameFilter, emailFilter, globalFilter,  orderBy);
			
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	//Get users by course
	@Transactional
	public Page<User> getUserByCourse(Pageable pageable, Long idcourse, String rol, Long id, String ciFilter,
			String roleFilter, String nameFilter, String emailFilter, String globalFilter, String orderBy) {
		Course course = courseRepo.findDistinctById(idcourse).orElseThrow(()-> new NoDataFoundException("Course not found."));
		//Get users by course, student or teacher
		List<Long> ids = (rol==null)? course.getInscriptions().stream().map(user->user.getUser().getId()).collect(Collectors.toList())
				:course.getTeachers().stream().map(user->user.getId()).collect(Collectors.toList());
		

		//Pageable AuxPageable = PageRequest.of(0, Integer.MAX_VALUE);
		Page<User> users = userFilters(null, id, ciFilter, roleFilter, nameFilter, emailFilter, globalFilter,  orderBy);
		
		List<User> idUsers = users.stream().filter(user->ids.contains(user.getId())).collect(Collectors.toList());
		
		return userRepo.findByIdInAndUserStatusTrue(pageable, idUsers.stream().map(user->user.getId()).collect(Collectors.toList()));
	}

	@Transactional
	public Optional<User> findById(Long id) {
		Optional<User> user = userRepo.findById(id);
		return user;
	}
	
	
	@Transactional
	public User deleteUser(Long idUser) {
		User existsUser = userRepo.findByIdAndUserStatus(idUser, true).orElse(null);
		if(existsUser == null) throw new NoDataFoundException("User not found.");
		List<Long> ids = new ArrayList<Long>();
		for (Role rol : existsUser.getRoles()) {
			if(rol.getName().equalsIgnoreCase(Roles.STUDENT.name())) {
				ids.addAll(existsUser.getInscriptions().stream().map(curso->curso.getCourse().getId()).collect(Collectors.toList()));
			}
			if(rol.getName().equalsIgnoreCase(Roles.TEACHER.name())) {
				ids.addAll(existsUser.getTeachedCourses().stream().map(curso->curso.getId()).collect(Collectors.toList()));
			}
		}
		for (Long idCourse : ids) {
			courseService.unregisterUserOfCourse(idCourse, idUser); 
		}			
		existsUser.setUserStatus(false);
		return userRepo.save(existsUser);
	}
	
	@Transactional
	public User create(User user) {
		try {
			user.setCi(formatCi(user.getCi()));
			UserValidator.create(user);
			User existsUser = userRepo.findByEmailIgnoreCase(user.getEmail()).orElse(null);
			User existsUserByCi = userRepo.findByCi(user.getCi()).orElse(null);
			if(user.getRoles() == null) throw new ValidateServiceException("El rol del usuario es requerido.");
			if(user.getId()!=null ) {
				User userUpdated = userRepo.findById(user.getId()).orElseThrow(()-> new NoDataFoundException("Usuario no existe."));
				if (existsUser != null && userUpdated.getId() != existsUser.getId())
					throw new ValidateServiceException("The user email already exists. Please, choose another one.");
				if (existsUserByCi != null && userUpdated.getId() != existsUserByCi.getId())
					throw new ValidateServiceException("The user ci already exists. Please, choose another one.");
				
				userUpdated.setBirthDate(user.getBirthDate());
				userUpdated.setFirstName(user.getFirstName());
				userUpdated.setLastName(user.getLastName());
				userUpdated.setEmail(user.getEmail());
				userUpdated.setUserDescription(user.getUserDescription());
				if(user.getProfileImage() == null || user.getProfileImage().isBlank()) {
					userUpdated.setProfileImage(defaultProfileImage());
				}else {
					userUpdated.setProfileImage(user.getProfileImage());
				}
				
				//Unregister techer if remove role teacher a user
				List<Course> teacherOnCourses = userUpdated.getTeachedCourses();
				if (teacherOnCourses != null && !user.getRoles().stream()
						.filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findAny().isPresent()) {
					for (int i=0; i< teacherOnCourses.size(); i++) {
						courseService.unregisterUserOfCourse(teacherOnCourses.get(i).getId(), user.getId());
					}
				}
				List<Inscription> inscription = userUpdated.getInscriptions();
				if(inscription!=null && !user.getRoles().stream()
						.filter(r -> r.getName().equalsIgnoreCase("STUDENT")).findFirst().isPresent()) {
					for (int i=0; i< inscription.size(); i++) {
						courseService.unregisterUserOfCourse(inscription.get(i).getCourse().getId(), user.getId());
					}
				}
				userUpdated.setRoles(user.getRoles());
				return userRepo.save(userUpdated);
			}else {
				//Si existe y esta borrado logicamnte lo habilito
				if (existsUser != null && !existsUser.isUserStatus() && existsUser.getEmail().equalsIgnoreCase(user.getEmail())
						&& existsUser.getCi().equalsIgnoreCase(user.getCi())) {
					user.setId(existsUser.getId());
					user.setProfileImage(existsUser.getProfileImage());
				}else {
					if(existsUser != null) throw new ValidateServiceException("The user email already exists. Please, choose another one.");
					if(existsUserByCi != null) throw new ValidateServiceException("The user ci already exists. Please, choose another one.");
				}
				String encryptedPassword = passwordEncoder.encode(user.getPassword()!=null?user.getPassword():user.getCi());
				user.setPassword(encryptedPassword);
				user.setTokenNotification("");
			}
			if(user.getProfileImage()==null || user.getProfileImage().isBlank()) {
				user.setProfileImage(defaultProfileImage());
			}
			return userRepo.save(user);
		} catch(ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch(Exception e) {
			log.info(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	// create default image profile
	private String defaultProfileImage() throws IOException {
		File file = new File(UPLOAD_FOLDER+"profileImage.png");
		FileInputStream fileInputStreamReader = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fileInputStreamReader.read(bytes);
		return "data:image/png;base64," + new String(Base64.encodeBase64(bytes), "UTF-8");
	}
	
	@Transactional
	public UserLoggedDTO login(UserLoginDTO request) {
		try {
			User user = userRepo.findByEmailIgnoreCase(request.getEmail()).orElseThrow(() -> new ValidateServiceException("User or password incorrect."));
			if(!user.isUserStatus()) throw new ValidateServiceException("User or password incorrect.");
			if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) throw new ValidateServiceException("User or password incorrect.");
			String token = createToken(user);
			return new UserLoggedDTO(userConverter.FromEntityWithProfileImg(user), token);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	public String createToken(User user) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + (1000*60*60));
		final String authorities = user.getRoles().stream()
				.map(Role::getName)
				.collect(Collectors.joining(","));

		return Jwts.builder()
				.setSubject(Long.toString(user.getId()))
				.claim("AUTHORITIES_KEY", authorities)
				.setIssuedAt(now)
				.setExpiration(expiration)
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (UnsupportedJwtException e) {
			log.error("JWT in a particular format/configuration that does not match the format expected.");
		} catch (MalformedJwtException e) {
			log.error("JWT was not correctly constructed and should be rejected.");
		} catch (SignatureException e) {
			log.error("Calculating a signature or verifying an existing signature of a JWT failed.");
		} catch (ExpiredJwtException e) {
			log.error("JWT was accepted after it expired and must be rejected.");
		}
		return false;
	}
	
	public String getSubjectFromToken(String token) {
		try {
			return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ValidateServiceException("Invalid token.");
		}
	}
	@Transactional
	public Page<User> getUsersByUsername(String email, Pageable pageable){
		try {
			return userRepo.findByEmailContainingIgnoreCase(email, pageable);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	@Transactional
	public List<User> createMultiUsers(MultipartFile file) {
		try {
			// if (!file.getContentType().equalsIgnoreCase("text/csv"))
				// throw new ValidateServiceException("The file type is not csv.");
			if(file.isEmpty()) throw new ValidateServiceException("File empty");
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOAD_FOLDER+"users.csv");
			File folder = new File(UPLOAD_FOLDER);
			folder.mkdirs();
			Files.deleteIfExists(path);
			Files.write(path, bytes);
			return readFile();
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	@Transactional
	private List<User> createUsers(List<User> user) throws Exception {
		return userRepo.saveAll(user);
	}
	
	//get user by id
	@Transactional
	public User getUser(Long id) {
		if(id==null) throw new ValidateServiceException("The id field is required.");
		return userRepo.findById(id).orElseThrow(() -> new NoDataFoundException("User not found."));
	}
	
	private List<User> readFile() {
		List<User> users = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(UPLOAD_FOLDER+"users.csv"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if(line.isBlank()) throw new ValidateServiceException("File empty"); 
		        String[] values = line.split(";");
		  
		        if(values[0].contains("[Content_Types].xml")) throw new ValidateServiceException("Formato de acrchivo no válido.");
				if (values.length < 6 || values[0].trim().isBlank() || values[1].trim().isBlank() || values[2].trim().isBlank()
						|| values[3].trim().isBlank() || values[4].trim().isBlank() || values[5].trim().isBlank()) {
			        throw new ValidateServiceException("No se permiten campos nulos.");
		        }
				if(!values[0].trim().matches(Kte.pattern_email)) throw new ValidateServiceException("Email "+values[0].trim()+" no válido.");
				
				User userAux = existsUser(values[0].toLowerCase(), formatCi(values[1])); 
				String rolesAux[] = values[5].trim().split(",");
				List<Role> roles = new ArrayList<Role>();
				for (String rol : rolesAux) {
					Role newRol = null;
					if(rol.equalsIgnoreCase(Roles.ADMIN.name())) {
						newRol = new Role();
						newRol.setName(Roles.ADMIN.name());
					}
					if(rol.equalsIgnoreCase(Roles.STUDENT.name())) {
						newRol = new Role();
						newRol.setName(Roles.STUDENT.name());
					}
					if(rol.equalsIgnoreCase(Roles.TEACHER.name())) {
						newRol = new Role();
						newRol.setName(Roles.TEACHER.name());
					}
					if(newRol != null) {
						roleRepo.save(newRol);
						roles.add(newRol);
					}
					
				}
				if(roles.isEmpty()) {
					throw new ValidateServiceException("Rol no válido para el usuario "+values[0].trim());
				}
				//String newPass = resetPassword();
				User user = new User();
				user.setRoles(roles);
				//user.setUsername(values[0].trim());
				String ci = formatCi(values[1].trim());
				user.setId(userAux.getId()!=null?userAux.getId():null);
				user.setEmail(values[0].trim().toLowerCase());
				user.setCi(ci);
				user.setFirstName(values[2]);
				user.setLastName(values[3]);
				user.setBirthDate(LocalDate.parse(values[4],  DateTimeFormatter.ofPattern(dateFormat)));
		        user.setPassword(passwordEncoder.encode(ci));
		        user.setProfileImage(userAux.getProfileImage()!=null?userAux.getProfileImage():defaultProfileImage());
		        user.setTokenNotification("");
		        users.add(user);
		    }
		    return createUsers(users);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	private String resetPassword() {
	    int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int size = 10;
	    Random random = new Random();
	 
	    String generatedPassword = random.ints(leftLimit, rightLimit + 1).limit(size)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	 
	    System.out.println(generatedPassword);
	    return generatedPassword;
	}
	
	@Transactional
	private User existsUser(String email, String ci) {
		User user = new User();
		User userEmail = userRepo.findByEmailIgnoreCase(email).orElse(null);
		User userCi = userRepo.findByCi(ci).orElse(null);
		//Si existe y esta borrado logicamnte lo habilito
		if (userEmail != null && !userEmail.isUserStatus() && userEmail.getEmail().equalsIgnoreCase(email)
				&& userEmail.getCi().equalsIgnoreCase(ci)) {
			user.setId(userEmail.getId());
			user.setUserStatus(true);
			user.setProfileImage(userEmail.getProfileImage());
		}else {
			if(userEmail!=null) throw new ValidateServiceException("Usuario con email "+email+" ya se encuentra registrado. Por favor, elija otro nuevo.");
			if(userCi!=null) throw new ValidateServiceException("Usuario con ci "+ci+" ya se encuentra registrado.");
		}
		return user;
	}
	
	@Transactional
	public User updateUserPassword(UpdatePasswordDTO request){
		try {
			User user = userRepo.findByEmailIgnoreCase(request.getEmail()).orElseThrow(() -> new NoDataFoundException("User not found."));
			String msg="";
			if(request.getPassword()==null || request.getPassword().isBlank()) {
				String newPassword = resetPassword();
				user.setPassword(passwordEncoder.encode(newPassword));
				msg +=  "Estimado " + user.getFirstName() + ", usted ha solicitado reestableser su contraseña.<br>"; 
				msg += "Su nueva contraseña es: " + newPassword+"<br>";
				msg += "<b>Equipo E-studia</b>"; 
			}else {
				user.setPassword(passwordEncoder.encode(request.getPassword()));
				msg +=  "Estimado " + user.getFirstName() + ", su contraseña fue modificada exitosamente.<br>";
			}
			User userUpdated = userRepo.save(user);
			SendMail.Send(userUpdated.getEmail(), "Cambio de contraseña.", msg);
			return userUpdated;
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	@Transactional
	public User setNotifications(NotificationDTO request){
		try {
			User user = userRepo.findByEmailIgnoreCase(request.getEmail()).orElseThrow(() -> new NoDataFoundException("User not found."));
			String msg="";
			if(request.isNotification()==false) {
				user.setNotifications(false);
				user.setTokenNotification("");
				msg += "Se han desactivado las notificaciones con éxito.";
			}else {
				user.setNotifications(true);
				user.setTokenNotification(request.getTokenNotification());
				msg += "Se han activado las notificaciones con éxito.";
			}
			User userUpdated = userRepo.save(user);
			return userUpdated;
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	//Function of users filters
	private Page<User> userFilters(Pageable pageable, Long id, String ciFilter, String roleFilter, String nameFilter,
			String emailFilter, String globalFilter, String orderBy) {
		String order = (orderBy == null) ? "" : orderBy;
		try {
			// Filtro todos los campos
			if (id != null && ciFilter != null && !ciFilter.isEmpty() && roleFilter != null && !roleFilter.isEmpty()
					&& nameFilter != null && !nameFilter.isEmpty() && emailFilter != null && !emailFilter.isEmpty()) {
				switch (order) {
				case "id":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderById(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), emailFilter.trim(),
									roleFilter.trim(), true);
				case "name":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), emailFilter.trim(),
									roleFilter.trim(), true);
				case "ci":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByCi(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), emailFilter.trim(),
									roleFilter.trim(), true);
				case "email":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), emailFilter.trim(),
									roleFilter.trim(), true);
				case "roles":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), emailFilter.trim(),
									roleFilter.trim(), true);
				default:
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), emailFilter.trim(),
									roleFilter.trim(), true);
				}
	
			}
			// Filtro todos los campos excepto email
			if (id != null && ciFilter != null && !ciFilter.isEmpty() && roleFilter != null && !roleFilter.isEmpty()
					&& nameFilter != null && !nameFilter.isEmpty()) {
				switch (order) {
				case "id":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderById(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), roleFilter.trim(), true);
				case "name":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), roleFilter.trim(), true);
				case "ci":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByCi(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), roleFilter.trim(), true);
				case "roles":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), roleFilter.trim(), true);
	
				default:
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), roleFilter.trim(), true);
				}
	
			}
			// Filtro todos los campos excepto roles
			if (id != null && ciFilter != null && !ciFilter.isEmpty() && emailFilter != null && !emailFilter.isEmpty()
					&& nameFilter != null && !nameFilter.isEmpty()) {
				switch (order) {
				case "id":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderById(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(),
									emailFilter.trim(), true);
				case "name":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByFirstName(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(),
									emailFilter.trim(), true);
				case "ci":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByCi(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(),
									emailFilter.trim(), true);
				case "email":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(),
									emailFilter.trim(), true);
	
				default:
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatus(
									pageable, id, nameFilter.trim(), nameFilter.trim(), ciFilter.trim(),
									emailFilter.trim(), true);
				}
	
			}
			// Filtro todos los campos excepto ci
			if (id != null && roleFilter != null && !roleFilter.isEmpty() && emailFilter != null && !emailFilter.isEmpty()
					&& nameFilter != null && !nameFilter.isEmpty()) {
				switch (order) {
				case "id":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderById(
									pageable, id, nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), emailFilter.trim(), true);
				case "name":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByFirstName(
									pageable, id, nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), emailFilter.trim(), true);
				case "roles":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByRolesName(
									pageable, id, nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), emailFilter.trim(),  true);
				case "email":
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, id, nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), emailFilter.trim(), true);
	
				default:
					return userRepo
							.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatus(
									pageable, id, nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), emailFilter.trim(), true);
				}
	
			}
			// Filtro todos los campos excepto id
			if (ciFilter != null && !ciFilter.isEmpty() && roleFilter != null && !nameFilter.isEmpty() && emailFilter != null && !emailFilter.isEmpty()
					&& nameFilter != null && !nameFilter.isEmpty()) {
				switch (order) {
				case "ci":
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByCi(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), emailFilter.trim(), true);
				case "name":
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByFirstName(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), emailFilter.trim(), true);
				case "roles":
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByRolesName(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), emailFilter.trim(), true);
				case "email":
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), emailFilter.trim(), true);
	
				default:
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndUserStatus(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), emailFilter.trim(), true);
				}
	
			}
			// Filtro todos los campos excepto id y ci
			if (roleFilter != null && !roleFilter.isEmpty()
					&& nameFilter != null && !nameFilter.isEmpty() && emailFilter != null && !emailFilter.isEmpty()) {
				switch (order) {
				
				case "name":
					return userRepo
							.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
									pageable, nameFilter.trim(), nameFilter.trim(), emailFilter.trim(), roleFilter.trim(), true);
				case "email":
					return userRepo
							.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, nameFilter.trim(), nameFilter.trim(), emailFilter.trim(), roleFilter.trim(), true);
				case "roles":
					return userRepo
							.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
									pageable, nameFilter.trim(), nameFilter.trim(), emailFilter.trim(), roleFilter.trim(), true);
				default:
					return userRepo
							.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
									pageable, nameFilter.trim(), nameFilter.trim(), emailFilter.trim(), roleFilter.trim(), true);
				}
	
			}
			// Filtro todos los campos excepto id y name
			if (roleFilter != null && !roleFilter.isEmpty()	&& ciFilter != null && !ciFilter.isEmpty() && emailFilter != null && !emailFilter.isEmpty()) {
				switch (order) {
				
				case "ci":
					return userRepo
							.findByCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByCi(
									pageable, ciFilter.trim(), emailFilter.trim(), roleFilter.trim(), true);
				case "email":
					return userRepo
							.findByCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, ciFilter.trim(), emailFilter.trim(), roleFilter.trim(), true);
				case "roles":
					return userRepo
							.findByCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
									pageable, ciFilter.trim(), emailFilter.trim(), roleFilter.trim(), true);
				default:
					return userRepo
							.findByCiContainingIgnoreCaseAndEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
									pageable, ciFilter.trim(), emailFilter.trim(), roleFilter.trim(), true);
				}
	
			}
			// Filtro todos los campos excepto id y email
			if (roleFilter != null && !roleFilter.isEmpty()	&& ciFilter != null && !ciFilter.isEmpty() && nameFilter != null && !nameFilter.isEmpty()) {
				switch (order) {
				
				case "ci":
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByCi(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), true);
				case "name":
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), true);
				case "roles":
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), true);
				default:
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), roleFilter.trim(), true);
				}
	
			}
			// Filtro todos los campos excepto role y email 
			if (id != null && ciFilter != null && !ciFilter.isEmpty() && nameFilter != null && !nameFilter.isEmpty()) {
				switch (order) {
				
				case "ci":
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatusOrderByCi(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), id, true);
				case "name":
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatusOrderByFirstName(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), id, true);
				case "id":
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatusOrderById(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), id, true);
				default:
					return userRepo
							.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndIdAndUserStatus(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), id, true);
				}
	
			}
			// Filtro todos los campos excepto role y name 
			if (id != null && emailFilter != null && !emailFilter.isEmpty() && ciFilter != null && !ciFilter.isEmpty()) {
				switch (order) {
				
				case "email":
					return userRepo
							.findByEmailContainingIgnoreCaseAndIdAndCiContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, emailFilter.trim(), id, ciFilter.trim(), true);
				case "id":
					return userRepo
							.findByEmailContainingIgnoreCaseAndIdAndCiContainingIgnoreCaseAndUserStatusOrderById(
									pageable, emailFilter.trim(), id, ciFilter.trim(), true);
				case "ci":
					return userRepo
							.findByEmailContainingIgnoreCaseAndIdAndCiContainingIgnoreCaseAndUserStatusOrderByCi(
									pageable, emailFilter.trim(), id, ciFilter.trim(), true);
				default:
					return userRepo
							.findByEmailContainingIgnoreCaseAndIdAndCiContainingIgnoreCaseAndUserStatus(
									pageable, emailFilter.trim(), id, ciFilter.trim(), true);
				}
	
			}
			// Filtro todos los campos excepto role e id 
			if (ciFilter != null && !ciFilter.isEmpty() && emailFilter != null && !emailFilter.isEmpty() && nameFilter != null && !nameFilter.isEmpty()) {
				switch (order) {
				
				case "email":
					return userRepo
							.findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, emailFilter.trim(), nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), true);
				case "name":
					return userRepo
							.findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatusOrderByFirstName(
									pageable, emailFilter.trim(), nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), true);
				case "ci":
					return userRepo
							.findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatusOrderByCi(
									pageable, emailFilter.trim(), nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), true);
				default:
					return userRepo
							.findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatus(
									pageable, emailFilter.trim(), nameFilter.trim(), nameFilter.trim(), ciFilter.trim(), true);
				}
	
			}
			// Filtro por id y email 
			if (id != null && emailFilter != null && !emailFilter.isEmpty() ) {
				switch (order) {
				
				case "id":
					return userRepo.findByIdAndEmailContainingIgnoreCaseAndUserStatusOrderById(
									pageable, id, emailFilter.trim(), true);
				case "email":
					return userRepo.findByIdAndEmailContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, id, emailFilter.trim(), true);
				default:
					return userRepo.findByIdAndEmailContainingIgnoreCaseAndUserStatus(
									pageable, id, emailFilter.trim(), true);
				}
			}
			// Filtro por id y name 
			if (id != null && nameFilter != null && !nameFilter.isEmpty() ) {
				switch (order) {
				
				case "id":
					return userRepo.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderById(
									pageable, id, nameFilter.trim(), nameFilter.trim(), true);
				case "name":
					return userRepo.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
									pageable, id, nameFilter.trim(), nameFilter.trim(), true);
				default:
					return userRepo.findByIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatus(
									pageable, id, nameFilter.trim(), nameFilter.trim(), true);
				}
			}
			// Filtro por id y ci 
			if (id != null && ciFilter != null && !ciFilter.isEmpty() ) {
				switch (order) {
				
				case "id":
					return userRepo.findByIdAndCiContainingIgnoreCaseAndUserStatusOrderById(
									pageable, id, ciFilter.trim(), true);
				case "ci":
					return userRepo.findByIdAndCiContainingIgnoreCaseAndUserStatusOrderByCi(
							pageable, id, ciFilter.trim(), true);
				default:
					return userRepo.findByIdAndCiContainingIgnoreCaseAndUserStatus(
							pageable, id, ciFilter.trim(), true);
				}
			}
			// Filtro por id y rol 
			if (id != null && roleFilter != null && !roleFilter.isEmpty() ) {
				switch (order) {
				
				case "id":
					return userRepo.findByIdAndRolesNameContainingIgnoreCaseAndUserStatusOrderById(
									pageable, id, roleFilter.trim(), true);
				case "role":
					return userRepo.findByIdAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
							pageable, id, roleFilter.trim(), true);
				default:
					return userRepo.findByIdAndRolesNameContainingIgnoreCaseAndUserStatus(
							pageable, id, roleFilter.trim(), true);
				}
			}
			// Filtro por email y name ordenado por email 
			if (emailFilter != null && !emailFilter.isEmpty() && nameFilter != null && !nameFilter.isEmpty() ) {
				switch (order) {
				
				case "email":
					return userRepo.findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, emailFilter.trim(), nameFilter.trim(), nameFilter.trim(), true);
				case "name":
					return userRepo.findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
							pageable, emailFilter.trim(), nameFilter.trim(), nameFilter.trim(), true);
				default:
					return userRepo.findByEmailContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatus(
							pageable, emailFilter.trim(), nameFilter.trim(), nameFilter.trim(), true);
				}
			}
			// Filtro por email y ci ordenado por email 
			if (emailFilter != null && !emailFilter.isEmpty() && ciFilter != null && !ciFilter.isEmpty() ) {
				switch (order) {
				
				case "email":
					return userRepo.findByEmailContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, emailFilter.trim(), ciFilter.trim(), true);
				case "ci":
					return userRepo.findByEmailContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatusOrderByCi(
							pageable, emailFilter.trim(), ciFilter.trim(), true);
				default:
					return userRepo.findByEmailContainingIgnoreCaseAndCiContainingIgnoreCaseAndUserStatus(
							pageable, emailFilter.trim(), ciFilter.trim(), true);
				}
			}
			// Filtro por email y rol ordenado por email 
			if (emailFilter != null && !emailFilter.isEmpty() && roleFilter != null && !roleFilter.isEmpty() ) {
				switch (order) {
				
				case "email":
					return userRepo.findByEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByEmail(
									pageable, emailFilter.trim(), roleFilter.trim(), true);
				case "rol":
					return userRepo.findByEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
							pageable, emailFilter.trim(), roleFilter.trim(), true);
				default:
					return userRepo.findByEmailContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
							pageable, emailFilter.trim(), roleFilter.trim(), true);
				}
			}
			// Filtro por ci y rol ordenado por ci 
			if (ciFilter != null && !ciFilter.isEmpty() && ciFilter != null && !ciFilter.isEmpty() ) {
				switch (order) {
				
				case "email":
					return userRepo.findByCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByCi(
									pageable, ciFilter.trim(), roleFilter.trim(), true);
				case "rol":
					return userRepo.findByCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(
							pageable, ciFilter.trim(), roleFilter.trim(), true);
				default:
					return userRepo.findByCiContainingIgnoreCaseAndRolesNameContainingIgnoreCaseAndUserStatus(
							pageable, ciFilter.trim(), roleFilter.trim(), true);
				}
			}
			// Filtro por ci y name ordenado por ci 
			if (ciFilter != null && !ciFilter.isEmpty() && ciFilter != null && !ciFilter.isEmpty() ) {
				switch (order) {
				
				case "ci":
					return userRepo.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByCi(
									pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), true);
				case "name":
					return userRepo.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
							pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), true);
				default:
					return userRepo.findByCiContainingIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatus(
							pageable, ciFilter.trim(), nameFilter.trim(), nameFilter.trim(), true);
				}
			}
			//Filtro por roles
			if (roleFilter != null && !roleFilter.isEmpty() ) {
				switch (order) {
				
				case "role":
					return userRepo.findByRolesNameContainingIgnoreCaseAndUserStatusOrderByRolesName(pageable, roleFilter.trim(), true);
				default:
					return userRepo.findByRolesNameContainingIgnoreCaseAndUserStatus(pageable, roleFilter.trim(), true);
				}
		
			}
			//Filtro por name
			if (nameFilter != null && !nameFilter.isEmpty() ) {
				switch (order) {
				
				case "name":
					return userRepo
							.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatusOrderByFirstName(
									pageable, nameFilter.trim(), nameFilter.trim(), true);
				default:
					return userRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndUserStatus(
							pageable, nameFilter.trim(), nameFilter.trim(), true);
				}
		
			}
			//Filtro por ci
			if (ciFilter != null && !ciFilter.isEmpty() ) {
				switch (order) {
				
				case "ci":
					return userRepo
							.findByCiContainingIgnoreCaseAndUserStatusOrderByCi(pageable, ciFilter.trim(), true);
				default:
					return userRepo.findByCiContainingIgnoreCaseAndUserStatus(pageable, ciFilter.trim(), true);
				}
			}
			//Filtro por email
			if (emailFilter != null && !emailFilter.isEmpty() ) {
				switch (order) {
				
				case "email":
					return userRepo
							.findByEmailContainingIgnoreCaseAndUserStatusOrderByEmail(pageable, emailFilter.trim(), true);
				default:
					return userRepo.findByEmailContainingIgnoreCaseAndUserStatus(pageable, emailFilter.trim(), true);
				}
			}
			//Filtro por id
			if (id != null) {
				switch (order) {
				
				case "id":
					return userRepo.findByIdAndUserStatusOrderById(pageable, id, true);
				default:
					return userRepo.findByIdAndUserStatus(pageable, id, true);
				}
			}
			//Filtro global
			if(globalFilter!=null && !globalFilter.isEmpty()) {
				nameFilter = globalFilter.trim();
				roleFilter = globalFilter.trim();
				ciFilter = globalFilter.trim();
				emailFilter = globalFilter.trim();
				return userRepo
						.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrRolesNameContainingIgnoreCaseOrCiContainingIgnoreCaseOrEmailContainingIgnoreCaseAndUserStatus(
								pageable, nameFilter, nameFilter, roleFilter, ciFilter, emailFilter, true);
			}
			//Order by record
			if("name".equalsIgnoreCase(order)) {
				return userRepo.findAllByAndUserStatusOrderByFirstName(pageable, true); 
			}
			if("id".equalsIgnoreCase(order)) {
				return userRepo.findAllByAndUserStatusOrderById(pageable, true); 
			}
			if("role".equalsIgnoreCase(order)) {
				return userRepo.findAllByAndUserStatusOrderByRolesName(pageable, true); 
			}
			if("ci".equalsIgnoreCase(order)) {
				return userRepo.findAllByAndUserStatusOrderByCi(pageable, true); 
			}
			if("email".equalsIgnoreCase(order)) {
				return userRepo.findAllByAndUserStatusOrderByEmail(pageable, true); 
			}
			return userRepo.findByUserStatus(true, pageable);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	private String formatCi(String ci) {
		String ciFormated = ci.replaceAll("[\"[^0-9]\"]", "");
		System.out.println(ciFormated);
		return ciFormated;
	}
	//Get users deleted
	public Page<User> getUsersDeleted(Pageable pageable, String emailFilter) {
		try {
			if(emailFilter==null) {
				return userRepo.findByUserStatus(false, pageable);
			}
			return userRepo.findByEmailContainingIgnoreCaseAndUserStatusOrderByEmail(pageable, emailFilter.trim(), false);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	//Enable user deleted
	public User enableUser(Long userId) {
		try {
			User existsUser = userRepo.findByIdAndUserStatus(userId, false).orElseThrow(()->new NoDataFoundException("User not found"));
			existsUser.setUserStatus(true);
			return existsUser;
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* Listar certificado */
	@Transactional(readOnly = true)
	public Certificate getUserCertificate(Long idCertificate) {
		try {
			return certificateRepo.findById(idCertificate).orElse(null);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
		
}
