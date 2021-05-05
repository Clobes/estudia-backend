package com.backend.estudia.controller;

import java.util.List;

import com.backend.estudia.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.estudia.converter.CertificateConverter;
import com.backend.estudia.converter.UserConverter;
import com.backend.estudia.entity.Certificate;
import com.backend.estudia.entity.User;
import com.backend.estudia.service.UserService;
import com.backend.estudia.util.WrapperResponse;
import com.backend.estudia.util.WrapperResponseList;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/users")
public class UserController {
		
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserConverter userConverter;
	
	@Autowired
	private CertificateConverter certificateConverter;
	
	@GetMapping
	@Transactional
	public ResponseEntity<WrapperResponseList<List<UserDTO>>> getUsers(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(value = "ciFilter", required = false) String ciFilter,
			@RequestParam(value = "roleFilter", required = false) String roleFilter,
			@RequestParam(value = "nameFilter", required = false) String nameFilter,
			@RequestParam(value = "emailFilter", required = false ) String emailFilter,
			@RequestParam(value = "globalFilter", required = false) String globalFilter,
			@RequestParam(value = "page", required = false, defaultValue = "0" ) int page,
			@RequestParam(value = "size", required = false, defaultValue = "5" ) int size
		){
		Pageable pageable = PageRequest.of(page, size);
		Page<User> users = userService.getUsersWithFilters(pageable, id, ciFilter, roleFilter, nameFilter, emailFilter, globalFilter, sortBy);
		long totalElements = users.getTotalElements();
		int totalPages = users.getTotalPages();
		WrapperResponseList<List<UserDTO>> response = new WrapperResponseList<List<UserDTO>>(true, totalElements,
				totalPages, "Users listed succesfully.", userConverter.fromEntity(users.toList()));
        return response.createResponse(HttpStatus.OK);
    }
	//Get users deleted
	@GetMapping("/disabled")
	@Transactional
	public ResponseEntity<WrapperResponseList<List<UserDTO>>> getUsersDeleted(
			@RequestParam(value = "emailFilter", required = false ) String emailFilter,
			@RequestParam(value = "page", required = false, defaultValue = "0" ) int page,
			@RequestParam(value = "size", required = false, defaultValue = "5" ) int size
		){
		Pageable pageable = PageRequest.of(page, size);
		Page<User> users = userService.getUsersDeleted(pageable, emailFilter);
		long totalElements = users.getTotalElements();
		int totalPages = users.getTotalPages();
		WrapperResponseList<List<UserDTO>> response = new WrapperResponseList<List<UserDTO>>(true, totalElements,
				totalPages, "Users listed succesfully.", userConverter.fromEntity(users.toList()));
        return response.createResponse(HttpStatus.OK);
    }
	
	//Get users by course
	@GetMapping("/course")
	@Transactional
	public ResponseEntity<WrapperResponseList<List<UserDTO>>> getUserByCourse(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(value = "ciFilter", required = false) String ciFilter,
			@RequestParam(value = "roleFilter", required = false) String roleFilter,
			@RequestParam(value = "nameFilter", required = false) String nameFilter,
			@RequestParam(value = "emailFilter", required = false ) String emailFilter,
			@RequestParam(value = "globalFilter", required = false) String globalFilter,
			@RequestParam(value = "idcourse", required = true) Long idcourse,
			@RequestParam(value = "byteacher", required = false) String rol,
			@RequestParam(value = "page", required = false, defaultValue = "0" ) int page,
			@RequestParam(value = "size", required = false, defaultValue = "5" ) int size
		){
		Pageable pageable = PageRequest.of(page, size);
		Page<User> users = userService.getUserByCourse(pageable, idcourse, rol, id, ciFilter, roleFilter, nameFilter, emailFilter, globalFilter, sortBy);
		long totalElements = users.getTotalElements();
		int totalPages = users.getTotalPages();
		WrapperResponseList<List<UserDTO>> response = new WrapperResponseList<List<UserDTO>>(true, totalElements,
				totalPages, "Users listed succesfully.", userConverter.fromEntity(users.toList()));
        return response.createResponse(HttpStatus.OK);
    }
	
	@GetMapping("/search")
	@Transactional
	public ResponseEntity<WrapperResponseList<List<UserDTO>>> getUsersByUsername(
			@RequestParam("email") String email,
			@RequestParam(value = "page", required = false, defaultValue = "0" ) int page,
			@RequestParam(value = "size", required = false, defaultValue = "5" ) int size
			){
		Pageable pageable = PageRequest.of(page, size);
		Page<User> users = userService.getUsersByUsername(email, pageable);
		long totalElements = users.getTotalElements();
		int totalPages = users.getTotalPages();
		WrapperResponseList<List<UserDTO>> response = new WrapperResponseList<List<UserDTO>>(true, totalElements,
				totalPages, "Users listed succesfully.", userConverter.fromEntity(users.toList()));
        return response.createResponse(HttpStatus.OK);
	}
	
	@PostMapping("/create")
	public ResponseEntity<WrapperResponse<UserDTO>> createUser(@RequestBody UserCreateDTO request) {
		User user = userService.create(userConverter.createUser(request));
		WrapperResponse<UserDTO> response = new WrapperResponse<UserDTO>(true, "User created succesfully.", userConverter.fromEntity(user));
		return response.createResponse(HttpStatus.CREATED);
	}
	
	@PostMapping("/login")
	@Transactional
	public ResponseEntity<WrapperResponse<UserLoggedDTO>> login(@RequestBody UserLoginDTO request) {
		UserLoggedDTO response = userService.login(request);
		return new WrapperResponse<>(true, "User logged succesfully.", response).createResponse(HttpStatus.OK);
	}
	
	/* Create multi users */
	@PostMapping("/create/users")
	@Transactional
	public ResponseEntity<WrapperResponse<List<UserDTO>>> addMultiUsers(@RequestParam("file") MultipartFile file) {
		List<User> users = userService.createMultiUsers(file);
		WrapperResponse<List<UserDTO>> response = new WrapperResponse<List<UserDTO>>(true,"Users created succesfully.", userConverter.fromEntity(users));
		return response.createResponse(HttpStatus.CREATED);
	}

	//Reset password
	@PutMapping("/reset/password")
	@Transactional
	public ResponseEntity<WrapperResponse<UserDTO>> updatePassword(@RequestBody UpdatePasswordDTO request){
			User response = userService.updateUserPassword(request);
			return new WrapperResponse<>(true, "Password reseted succesfully.", userConverter.fromEntity(response)).createResponse(HttpStatus.NO_CONTENT);
	}

	//Reset password
	@PutMapping("/notifications")
	@Transactional
	public ResponseEntity<WrapperResponse<UserDTO>> updateNotifications(@RequestBody NotificationDTO request){
		User response = userService.setNotifications(request);
		return new WrapperResponse<>(true, "Updated notifications.", userConverter.fromEntity(response)).createResponse(HttpStatus.NO_CONTENT);
	}
	//Enable User
	@PutMapping("/enable")
	@Transactional
	public ResponseEntity<WrapperResponse<UserDTO>> enableUser(@RequestParam("userId") Long userId){
		User user = userService.enableUser(userId);
		WrapperResponse<UserDTO> response = new WrapperResponse<UserDTO>(true, "User updated succesfully.", userConverter.fromEntity(user));
		return response.createResponse(HttpStatus.NO_CONTENT);
	}
	
	//Update User
	@PutMapping("/update")
	@Transactional
	public ResponseEntity<WrapperResponse<UserDTO>> updateUser(@RequestBody UserUpdateDTO request){
		User user = userService.create(userConverter.updateUser(request));
		WrapperResponse<UserDTO> response = new WrapperResponse<UserDTO>(true, "User updated succesfully.", userConverter.FromEntityWithProfileImg(user));
		return response.createResponse(HttpStatus.OK);
	}
	//Delete user
	@DeleteMapping("/delete")
	@Transactional
	public ResponseEntity<WrapperResponse<UserDTO>> deleteUser(@RequestParam Long id){
		User response = userService.deleteUser(id);
		return new WrapperResponse<>(true, "User deleted succesfully.", userConverter.fromEntity(response)).createResponse(HttpStatus.NO_CONTENT);
	}
	
	//Get profile user
	@GetMapping("/profile")
	@Transactional
	public ResponseEntity<WrapperResponse<UserDTO>> getUser(@RequestParam Long id){
		User response = userService.getUser(id);
		return new WrapperResponse<>(true, "User listed succesfully.", userConverter.FromEntityWithProfileImg(response)).createResponse(HttpStatus.OK);
	}
	
	/* Listar certificado */
	@GetMapping("/certificate")
	public ResponseEntity<WrapperResponse<CertificateDTO>> getCertificate(@RequestParam("idCertificate") Long request){
		Certificate certificate = userService.getUserCertificate(request);
		return new WrapperResponse<>(true, "Certificate listed successfully", certificateConverter.fromEntity(certificate)).createResponse(HttpStatus.OK);
	}
}
