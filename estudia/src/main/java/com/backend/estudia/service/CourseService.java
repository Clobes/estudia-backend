package com.backend.estudia.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backend.estudia.entity.Calification;
import com.backend.estudia.entity.Certificate;
import com.backend.estudia.entity.Course;
import com.backend.estudia.entity.Forum;
import com.backend.estudia.entity.Inscription;
import com.backend.estudia.entity.Line;
import com.backend.estudia.entity.Topic;
import com.backend.estudia.entity.User;
import com.backend.estudia.exception.GeneralServiceException;
import com.backend.estudia.exception.NoDataFoundException;
import com.backend.estudia.exception.ValidateServiceException;
import com.backend.estudia.repository.ICalificationRepository;
import com.backend.estudia.repository.ICertificateRepo;
import com.backend.estudia.repository.ICourseRepository;
import com.backend.estudia.repository.ILineRepository;
import com.backend.estudia.repository.ITopicRepository;
import com.backend.estudia.repository.IUserRepository;
import com.backend.estudia.util.ForumType;
import com.backend.estudia.util.LineType;
import com.backend.estudia.util.Roles;
import com.backend.estudia.util.SendMail;
import com.backend.estudia.validator.CourseValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CourseService {
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private ICourseRepository courseRepo;
	
	@Autowired
	private ITopicRepository topicRepo;
	
	@Autowired
	private ILineRepository lineRepo;
	
	@Autowired
	private IUserRepository userRepo;
	
	@Autowired
	private ICalificationRepository calificationRepo;
	
	@Autowired
	private ICertificateRepo certificateRepo;
	
	@Value("${config.path.static.file}")
	private String UPLOAD_FOLDER;
	
	@Value("${config.certificateURL}")
	private String certificateURL;
	
	
	/*Cursos*/
	//Get all courses with filters
	@Transactional
	public Page<Course> getAllCoursesWithFilter(Pageable pageable, String nameFilter, String areaFilter,
			String descriptionFilter, String globalFilter, String orderBy) {
		try {
			String order = (orderBy==null)?"":orderBy;
			if (nameFilter != null && !nameFilter.isEmpty() && areaFilter != null && !areaFilter.isEmpty()
					&& descriptionFilter != null && !descriptionFilter.isEmpty()) {
				switch(order)
				{
				   case "name" :
				      return courseRepo.findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByName(pageable, nameFilter.trim(), areaFilter.trim(),
								descriptionFilter.trim());   
				   case "area" :
					   return courseRepo.findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByArea(pageable, nameFilter.trim(), areaFilter.trim(),
								descriptionFilter.trim());
				   case "description" :
					   return courseRepo.findByNameIgnoreCaseContainingAndAreaIgnoreCaseContainingAndDescriptionIgnoreCaseContainingOrderByDescription(pageable, nameFilter.trim(), areaFilter.trim(),
								descriptionFilter.trim());
				   default : 
					   return courseRepo.findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCase(pageable, nameFilter.trim(), areaFilter.trim(),
								descriptionFilter.trim());
				}
				
			}
			if(nameFilter != null && !nameFilter.isEmpty() && areaFilter != null && !areaFilter.isEmpty()) {
				switch(order)
				{
				   case "name" :
				      return courseRepo.findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseOrderByName(pageable, nameFilter.trim(), areaFilter.trim());   
				   case "area" :
					   return courseRepo.findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseOrderByArea(pageable, nameFilter.trim(), areaFilter.trim());
				     
				   default : 
					   return courseRepo.findByNameContainingIgnoreCaseAndAreaContainingIgnoreCase(pageable, nameFilter.trim(), areaFilter.trim());
				}
				
			}
			if(nameFilter != null && !nameFilter.isEmpty() && descriptionFilter != null && !descriptionFilter.isEmpty()) {
				switch(order)
				{
				   case "name" :
					   return courseRepo.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByName(pageable, nameFilter.trim(), descriptionFilter.trim()); 
				   case "description" :
					   return courseRepo.findByNameContainingIgnoreCaseAndAreaContainingIgnoreCaseOrderByArea(pageable, nameFilter.trim(), areaFilter.trim());
				     
				   default : 
					   return courseRepo.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(pageable, nameFilter.trim(), descriptionFilter.trim());
				}
				
			}
			if(areaFilter != null && !areaFilter.isEmpty() && descriptionFilter != null && !descriptionFilter.isEmpty()) {
				switch(order)
				{
				   case "area" :		 	
					   return courseRepo.findByAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByArea(pageable, nameFilter.trim(), descriptionFilter.trim()); 
				   case "description" :
					   return courseRepo.findByAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCaseOrderByDescription(pageable, areaFilter.trim(), descriptionFilter.trim());
				     
				   default : 
					   return courseRepo.findByAreaContainingIgnoreCaseAndDescriptionContainingIgnoreCase(pageable, areaFilter.trim(),	descriptionFilter.trim());
				}
				
			}
			if(nameFilter != null && !nameFilter.isEmpty()) {
				switch(order)
				{
				   case "name" :		 	
					   return courseRepo.findByNameContainingIgnoreCaseOrderByName(pageable, nameFilter.trim()); 
				   				     
				   default : 
					   return courseRepo.findByNameContainingIgnoreCase(pageable, nameFilter.trim());
				}
				
			}
			if(areaFilter != null && !areaFilter.isEmpty()) {
				switch(order)
				{
				   case "area" :		 	
					   return courseRepo.findByAreaContainingIgnoreCaseOrderByArea(pageable, areaFilter.trim()); 
				   				     
				   default : 
					   return courseRepo.findByAreaContainingIgnoreCase(pageable, areaFilter.trim());
				}
				
			}
			if(descriptionFilter != null && !descriptionFilter.isEmpty()) {
				switch(order)
				{
				   case "description" :		 	
					   return courseRepo.findByDescriptionContainingIgnoreCaseOrderByDescription(pageable, descriptionFilter.trim()); 
				   				     
				   default : 
					   return courseRepo.findByDescriptionContainingIgnoreCase(pageable, descriptionFilter.trim());
				}
				
			}
			if(globalFilter!=null && !globalFilter.isEmpty()) {
				nameFilter = globalFilter;
				areaFilter = globalFilter;
				descriptionFilter = globalFilter;
				return courseRepo.findByNameContainingIgnoreCaseOrAreaContainingIgnoreCaseOrDescriptionContainingIgnoreCase(pageable, nameFilter.trim(), areaFilter.trim(),
						descriptionFilter.trim());
			}
			//Order by by record
			if("name".equalsIgnoreCase(order)) {
				return courseRepo.findAllByOrderByName(pageable); 
			}
			if("area".equalsIgnoreCase(order)) {
				return courseRepo.findAllByOrderByArea(pageable);  
			}
			if("description".equalsIgnoreCase(order)) {
				return courseRepo.findAllByOrderByDescription(pageable);  
			}
			return courseRepo.findAll(pageable);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	//Get courses by user
	@Transactional
	public Page<Course> getCoursesByUser(Pageable pageable, Long id, String rol) {
		User user = userRepo.findByIdAndUserStatus(id, true).orElseThrow(()-> new NoDataFoundException("User not found."));
		//Get courses by user, student or teacher
		List<Long> ids = (rol==null)? user.getInscriptions().stream().map(curso->curso.getCourse().getId()).collect(Collectors.toList())
				:user.getTeachedCourses().stream().map(curso->curso.getId()).collect(Collectors.toList());
		return courseRepo.findByIdIn(pageable, ids);
	}
	//Get course by id
	@Transactional
	public Course getCourseById(Long id) {
		if(id==null) throw new ValidateServiceException("The id field is required.");
		Course course = courseRepo.findDistinctById(id).orElseThrow(()->new NoDataFoundException("User not found."));
		System.out.println(course.toString());
		return course;
	}
	
	//Delete course by id
	@Transactional
	public void deleteUser(Long idCourse) {
		try {
			courseRepo.findById(idCourse).orElseThrow(()-> new NoDataFoundException("Course not found."));
			courseRepo.customDeleteCourseById(idCourse);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	//Create course
	@Transactional
	public Course create(Course course) {
		try {
			CourseValidator.create(course);
			Course existsCourse = courseRepo.findByName(course.getName()).orElse(null);
			if(existsCourse != null) throw new ValidateServiceException("The course name already exists. Please, choose another one.");
			Topic topic = new Topic("General", "Tema por defecto");
			topic.setLines(createDefaultForum());
			List<Topic> topics = new ArrayList<>();
			topics.add(topic);
			topicRepo.saveAll(topics);
			course.setTopics(topics);
			course.setCreationDate(LocalDateTime.now());
			return courseRepo.save(course);
		} catch(ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch(Exception e) {
			log.info(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	//create multi courses
	@Transactional
	public List<Course> createMultiUser(MultipartFile file) {
		try {
			if(file.isEmpty()) throw new ValidateServiceException("File empty");
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOAD_FOLDER+"courses.csv");
			File folder = new File(UPLOAD_FOLDER);
			folder.mkdirs();
			Files.deleteIfExists(path);
			Files.write(path, bytes);
			return readFileCourses();
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}	
	//Update Course
	@Transactional
	public Course update(Course course) {
		CourseValidator.update(course);
		Course courseUpdated = courseRepo.findById(course.getId()).orElseThrow(()-> new NoDataFoundException("Course not found."));
		courseUpdated.setAmountHours(course.getAmountHours());
		courseUpdated.setArea(course.getArea());
		courseUpdated.setCertification(course.isCertification());
		courseUpdated.setMinimunApproval(course.getMinimunApproval());
		courseUpdated.setName(course.getName());
		courseUpdated.setDescription(course.getDescription());
		return courseRepo.save(courseUpdated);
	}
	// Create default forum
	private List<Line> createDefaultForum(){
		List<Line> lines = new ArrayList<>();
		Forum forumNews = new Forum();
		forumNews.setTitle("Novedades");
		forumNews.setDescription("Foro de Novedades");
		forumNews.setForumType(ForumType.NEWS);
		forumNews.setLineType(LineType.FORUM);
		lines.add(forumNews);
		Forum forumQuestions = new Forum();
		forumQuestions.setTitle("Foro de Consultas");
		forumQuestions.setDescription("Foro de Consultas");
		forumQuestions.setForumType(ForumType.QUESTIONS);
		forumQuestions.setLineType(LineType.FORUM);
		lines.add(forumQuestions);
		return lineRepo.saveAll(lines);
	}
	
	/* Registrar usuario en un curso */
	@Transactional
	public void registerUserOnCourse(Long idCourse, Long idUser, Roles role) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElse(null);
			if(existsCourse == null) throw new NoDataFoundException("Course not found.");
			User existsUser = userRepo.findById(idUser).orElse(null);
			if(existsUser == null) throw new NoDataFoundException("User not found.");
			/* Verifico si está inscripto */
			if(existsStudentOnCourse(existsCourse, existsUser)) throw new ValidateServiceException("The user is already registered as student.");
			if(existsTeacherOnCourse(existsCourse, existsUser)) throw new ValidateServiceException("The user is already registered as teacher.");
			/* Verifico si es en calidad de estudiante o docente */
			if(role == Roles.STUDENT) {
				registerStudentOnCourse(existsCourse, existsUser);
			}else {
				registerTeacherOnCourse(existsCourse, existsUser);
			}
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* Registrar a un estudiante */
	private void registerStudentOnCourse(Course course, User user) {
		try {
			if(user.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("STUDENT")).findAny().orElse(null) == null) throw new ValidateServiceException("The user has no STUDENT role.");
			Inscription inscription = new Inscription();
			inscription.setCourse(course);
			inscription.setUser(user);
			course.getInscriptions().add(inscription);
			courseRepo.save(course);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* Verificar que un alumno esté en un curso */
	public boolean existsStudentOnCourse(Course course, User user) {
		try {
			Inscription ins = course.getInscriptions().stream().filter(i -> i.getUser().getId() == user.getId()).findAny().orElse(null);
			if(ins == null) {
				return false;
			}else {
				return true;
			}
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* Registrar a un docente */
	@Transactional
	private void registerTeacherOnCourse(Course course, User user) {
		try {
			if(user.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findAny().orElse(null) == null) throw new ValidateServiceException("The user has no TEACHER role.");
			course.getTeachers().add(user);
			user.getTeachedCourses().add(course);
			courseRepo.save(course);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* Verificar que un docente esté en un curso */
	public boolean existsTeacherOnCourse(Course course, User user) {
		try {
			User us = course.getTeachers().stream().filter(u -> u.getId() == user.getId()).findAny().orElse(null);
			if(us == null) {
				return false;
			}else {
				return true;
			}
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* Desasignar usuario de un curso */
	@Transactional
	public void unregisterUserOfCourse(Long idCourse, Long idUser) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElse(null);
			if(existsCourse == null) throw new NoDataFoundException("Course not found.");
			User existsUser = userRepo.findById(idUser).orElse(null);
			if(existsUser == null) throw new NoDataFoundException("User not found.");
			/* Verifico si está inscripto */
			if(!existsStudentOnCourse(existsCourse, existsUser) && !existsTeacherOnCourse(existsCourse, existsUser)) throw new ValidateServiceException("The user is not registered on this course.");
			/* Verifico si es en calidad de estudiante, se deben eliminar las entregas en ese curso */
			if(existsStudentOnCourse(existsCourse, existsUser)) {
				/* Debo eliminar las entregas - Queda pendiente hasta que Santiago implemente las tareas. */
				/* Elimino al estudiante */
				Inscription ins = existsCourse.getInscriptions().stream().filter(i -> i.getId().getUserId() == idUser).findFirst().orElse(null);
				List<Inscription> inscriptions = existsCourse.getInscriptions();
				inscriptions.remove(ins);
				courseRepo.save(existsCourse);
			}else {
				/* Elimino al docente */
				existsUser.getTeachedCourses().remove(existsCourse);
				userRepo.save(existsUser);
			}
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	//Bind or unbind users on course
	@Transactional
	public void registerUnregisterMultiUser(MultipartFile file, String unregister) {
		try {
			if(file.isEmpty()) throw new ValidateServiceException("File empty");
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOAD_FOLDER+"register_user.csv");
			File folder = new File(UPLOAD_FOLDER);
			folder.mkdirs();
			Files.deleteIfExists(path);
			Files.write(path, bytes);
			readFile(unregister);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
		
	}

	private void readFile(String unregister) {
		try (BufferedReader br = new BufferedReader(new FileReader(UPLOAD_FOLDER+"register_user.csv"))) {
		    String line;
		    Roles rol = null;
		    while ((line = br.readLine()) != null) {
		    	if(line.isBlank()) throw new ValidateServiceException("File empty"); 
		        String[] values = line.split(";");
		  
		        if(values[0].contains("[Content_Types].xml")) throw new ValidateServiceException("The file type is not csv.");
		        if(unregister==null) {
					if ((values.length < 3) || values[0].trim().isBlank() || values[1].trim().isBlank() || values[2].trim().isBlank()) {
				        throw new ValidateServiceException("The records were not created. One or more fields are blank.");
			        }
					rol = getRol(values[2].trim().toUpperCase());
					if(rol==null) throw new ValidateServiceException("Rol "+values[2].trim()+" not exist for user "+values[0].trim());
		        }else {
		        	if ((values.length < 2) || values[0].trim().isBlank() || values[1].trim().isBlank()) {
				        throw new ValidateServiceException("The records were not created. One or more fields are blank.");
			        }
		        }

				//if(!values[0].trim().matches(Kte.pattern_email)) throw new ValidateServiceException("Email "+values[0].trim()+" is not valid.");
								
				bindOrUnbindUserOnCourse(values[0].trim(), values[1].trim(), rol, unregister);
		    }
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	private Roles getRol(String rol) {
		for (Roles r : Roles.values()) { 
			if (r.name().equalsIgnoreCase(rol.trim())) return r; 
		} return null;
	}

	private void bindOrUnbindUserOnCourse(String ci, String name, Roles role, String unregister){
		Long idUser = userRepo.findByCiAndUserStatusTrue(ci).orElseThrow(()->new NoDataFoundException("User not found.")).getId();
		Long idCourse = courseRepo.findByName(name).orElseThrow(()->new NoDataFoundException("Course not found.")).getId();
		
		if (unregister==null) registerUserOnCourse(idCourse, idUser, role);
		else unregisterUserOfCourse(idCourse, idUser);
	}
	
	private List<Course> readFileCourses() {
		List<Course> courses = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(UPLOAD_FOLDER+"courses.csv"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if(line.isBlank()) throw new ValidateServiceException("File empty"); 
		        String[] values = line.split(";");
		  
		        if(values[0].contains("[Content_Types].xml")) throw new ValidateServiceException("The file type is not csv.");
				if (values.length < 3 || values[0].trim().isBlank() || values[1].trim().isBlank() || values[2].trim().isBlank()) {
			        throw new ValidateServiceException("The records were not created. One or more fields are blank.");
		        }
				Course existsCourses = courseRepo.findByName(values[0].trim()).orElse(null);
				if(existsCourses!=null ) throw new ValidateServiceException("Course " + values[0].trim() + " already exists. Please, choose another one.");
				Course course = new Course();
				course.setName(values[0].trim());
				course.setArea(values[1].trim());
				course.setDescription(values[2].trim());
				courses.add(create(course));
		    }
		    return courses;
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* ##### Calificaciones ##### */
	
	/* Calificar */
	@Transactional
	public void saveCalification(Calification calification, Long idCourse, Long idTeacher, Long idStudent) {
		try {
			CourseValidator.calificationCreate(calification);
			Course existsCourse = courseRepo.findById(idCourse).orElse(null);
			if(existsCourse == null) throw new NoDataFoundException("Course not found.");
			User existsTeacher = userRepo.findById(idTeacher).orElse(null);
			if(existsTeacher == null) throw new NoDataFoundException("Teacher user not found.");
			/* Verifico si el docente es docente del curso */
			if(!existsTeacherOnCourse(existsCourse, existsTeacher)) throw new ValidateServiceException("The user is not a techer of this course.");
			User existsStudent = userRepo.findById(idStudent).orElse(null);
			if(existsStudent == null) throw new NoDataFoundException("Student user not found.");
			/* Verifico si el estudiante es estudiante del curso */
			if(!existsStudentOnCourse(existsCourse, existsStudent)) throw new ValidateServiceException("The user is not a student of this course.");
			calification.setUser(existsStudent);
			calification.setCreationDate(LocalDateTime.now());
			calification.setCourse(existsCourse);
			existsCourse.getCalifications().add(calification);
			existsStudent.getCalifications().add(calification);
			courseRepo.save(existsCourse);
			if(existsStudent.isNotifications()) {
				notificationService.sendNotification("Calificacion", "Se ha publicado su calificacion del curso ".concat(existsCourse.getName()), existsStudent.getTokenNotification());
			}
			/* Se genera el certificado si el curso lleva certificación y el usuario llegó a la nota mínima */
			if(existsCourse.isCertification() && calification.getScore() >= existsCourse.getMinimunApproval()) {
				Certificate certificate = Certificate.builder()
						.firstName(existsStudent.getFirstName())
						.lastaName(existsStudent.getLastName())
						.ci(existsStudent.getCi())
						.courseName(existsCourse.getName())
						.courseDescription(existsCourse.getDescription())
						.amountHours(existsCourse.getAmountHours())
						.approvalDate(calification.getCreationDate())
						.score(calification.getScore())
						.build();
				certificateRepo.save(certificate);
				/* Se envía mail con certificado */
				String msg = "Estimado " + existsStudent.getFirstName() + ", se ha publicado tu calificación en el curso " + existsCourse.getName() + ".<br>"; 
				msg += "Calificación: " + calification.getScore() + ". <br>";
				msg += "Devolución: " +calification.getDevolution() + " <br>";
				msg += "Puedes ver tu certificado  de aprobación en " + certificateURL + certificate.getId() + "<br>";
				msg += "Quedará disponible para que lo puedas agregar en tu currículum.<br>";
				msg += "Gracias por confiar en nuestra plataforma.<br>";
				msg += "<b>Equipo e-Studia</b>";
				SendMail.Send(existsStudent.getEmail(), "Calificación final - " + certificate.getCourseName(), msg);
			}else {
				/* Se envía aviso vía mail a todos los estudiantes del curso */
				String msg = "Estimado " + existsStudent.getFirstName() + ", se ha publicado tu calificación en el curso " + existsCourse.getName() + ".<br>"; 
				msg += "Calificación: " + calification.getScore() + ". <br>";
				msg += "Devolución: " + calification.getDevolution() + " <br>";
				msg += "Gracias por confiar en nuestra plataforma.<br>";
				msg += "<b>Equipo e-Studia</b>";
				SendMail.Send(existsStudent.getEmail(), "Calificación final - " + existsCourse.getName(), msg);
			}
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* Visualizar calificaciones finales */
	@Transactional
	public List<Calification> getCalifications(Long idStudent) {
		try {
			User existsStudent = userRepo.findById(idStudent).orElse(null);
			if(existsStudent == null) throw new NoDataFoundException("Student user not found.");
			return calificationRepo.findCalificationsByUserId(idStudent);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	@Transactional
	public void saveMultiCalification(Long idTeacher, MultipartFile file) {
		try {
			if(file.isEmpty()) throw new ValidateServiceException("File empty");
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOAD_FOLDER+"calification_user.csv");
			File folder = new File(UPLOAD_FOLDER);
			folder.mkdirs();
			Files.deleteIfExists(path);
			Files.write(path, bytes);
			readFileCalification(idTeacher);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
		
	}
	private void readFileCalification(Long idTeacher) {
		try (BufferedReader br = new BufferedReader(new FileReader(UPLOAD_FOLDER+"calification_user.csv"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if(line.isBlank()) throw new ValidateServiceException("File empty"); 
		        String[] values = line.split(";");
		        if(values[0]!=null && !values[0].contains("Asignatura")) {
		        	if(values[0].contains("[Content_Types].xml")) throw new ValidateServiceException("The file type is not csv.");
					if (values.length < 4 || values[0].trim().isBlank() || values[1].trim().isBlank() || values[2].trim().isBlank()
							|| values[3].trim().isBlank()) {
				        throw new ValidateServiceException("The records were not created. One or more fields are blank.");
			        }
					Course existsCourse = courseRepo.findByName(values[0].trim()).orElse(null);
					if(existsCourse==null) throw new NoDataFoundException("Course "+values[0].trim()+" not found.");
					User existsTeacher = userRepo.findById(idTeacher).orElse(null);
					if(existsTeacher==null) throw new NoDataFoundException("Teacher not found.");
					User existsStudent = userRepo.findByCi(values[1].trim()).orElse(null);
					if(existsStudent==null) throw new NoDataFoundException("Student "+values[1].trim()+" not found.");
					Double cal = Double.parseDouble(values[2].trim().isEmpty()?"0":values[2].trim().replace(",", "."));
					if(cal < 0 && cal > 100) throw new ValidateServiceException("The grade for student "+values[1].trim()+" is not valid.");
					Calification calification = new Calification();
					calification.setScore(cal);
					calification.setDevolution(values[3].trim());
					saveCalification(calification, existsCourse.getId(), existsTeacher.getId(), existsStudent.getId());
		        }
		        
		    }
		    
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
		
	}

}
