package com.backend.estudia.controller;

import java.util.List;

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

import com.backend.estudia.converter.CalificationConverter;
import com.backend.estudia.converter.CourseConverter;
import com.backend.estudia.dto.CalificationDTO;
import com.backend.estudia.dto.CourseCreateDTO;
import com.backend.estudia.dto.CourseDTO;
import com.backend.estudia.dto.CourseSimpleDTO;
import com.backend.estudia.dto.RegisterUserOnCourseDTO;
import com.backend.estudia.dto.SaveCalificationDTO;
import com.backend.estudia.entity.Calification;
import com.backend.estudia.entity.Course;
import com.backend.estudia.service.CourseService;
import com.backend.estudia.util.WrapperResponse;
import com.backend.estudia.util.WrapperResponseList;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/course")
public class CourseController {
		
	@Autowired
	private CourseConverter courseConvenrter;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private CalificationConverter calificationConverter;
	
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<WrapperResponse<CourseDTO>> createCourse(@RequestBody CourseCreateDTO request) {
		Course course = courseService.create(courseConvenrter.createCourse(request));
		WrapperResponse<CourseDTO> response = new WrapperResponse<CourseDTO>(true, "Course created succesfully.", courseConvenrter.fromEntity(course));
		return response.createResponse(HttpStatus.CREATED);
	}
	/* Creacion masiva de cursos */
	@PostMapping("/create/courses")
	@Transactional
	public ResponseEntity<WrapperResponse<List<CourseDTO>>> createMultiCourse(
			@RequestParam("file") MultipartFile file
			){
		List<Course> courses = courseService.createMultiUser(file);
		WrapperResponse<List<CourseDTO>> response = new WrapperResponse<List<CourseDTO>>(true,"Users created succesfully.", courseConvenrter.fromEntity(courses));
        return response.createResponse(HttpStatus.CREATED);
	}
	//Get all courses 
	@GetMapping("/all")
	@Transactional
	public ResponseEntity<WrapperResponseList<List<CourseSimpleDTO>>> getCourses(
			@RequestParam(value = "nameFilter", required = false) String nameFilter,
			@RequestParam(value = "areaFilter", required = false) String areaFilter,
			@RequestParam(value = "globalFilter", required = false) String globalFilter,
			@RequestParam(value = "descriptionFilter", required = false ) String descriptionFilter,
			@RequestParam(value = "sortBy", required = false) String sortBy,
			@RequestParam(value = "page", required = false, defaultValue = "0" ) int page,
			@RequestParam(value = "size", required = false, defaultValue = "5" ) int size
			){
		Pageable pageable = PageRequest.of(page, size);
		Page<Course> courses = courseService.getAllCoursesWithFilter(pageable, nameFilter, areaFilter, descriptionFilter, globalFilter, sortBy);
		long totalElements = courses.getTotalElements();
		int totalPages = courses.getTotalPages();
		WrapperResponseList<List<CourseSimpleDTO>> response = new WrapperResponseList<List<CourseSimpleDTO>>(true, totalElements,
				totalPages, "Course listed succesfully.", courseConvenrter.fromSimpleEntity(courses.toList()));
        return response.createResponse(HttpStatus.OK);
    }
	//Get course by id
	@GetMapping
	@Transactional
	public ResponseEntity<WrapperResponse<CourseDTO>> getCourseById(@RequestParam(value = "id", required = true) Long id){
		Course course = courseService.getCourseById(id);
		return new WrapperResponse<>(true, "Course listed succesfully.", courseConvenrter.fromEntity(course)).createResponse(HttpStatus.OK);
	}
	
	//Get courses by user
	@GetMapping("/user")
	@Transactional
	public ResponseEntity<WrapperResponseList<List<CourseDTO>>> getCoursesByUser(
			@RequestParam(value = "iduser", required = true) Long id,
			@RequestParam(value = "byteacher", required = false) String rol,
			@RequestParam(value = "page", required = false, defaultValue = "0" ) int page,
			@RequestParam(value = "size", required = false, defaultValue = "5" ) int size
			){
		Pageable pageable = PageRequest.of(page, size);
		Page<Course> courses = courseService.getCoursesByUser(pageable, id, rol);
		long totalElements = courses.getTotalElements();
		int totalPages = courses.getTotalPages();
		WrapperResponseList<List<CourseDTO>> response = new WrapperResponseList<List<CourseDTO>>(true, totalElements,
				totalPages, "Course listed succesfully.", courseConvenrter.fromEntity(courses.toList()));
	    return response.createResponse(HttpStatus.OK);
	}
	//Delete course
	@DeleteMapping("/delete")
	@Transactional
	public ResponseEntity<WrapperResponse<CourseDTO>> deleteUser(
			@RequestParam(value = "id", required = true) Long id
			){
		courseService.deleteUser(id);
		WrapperResponse<CourseDTO> response = new WrapperResponse<CourseDTO>(true, "Course deleted succesfully.", null);
		return response.createResponse(HttpStatus.OK);
	}
	
	//Update course
	@PutMapping("/update")
	@Transactional
	public ResponseEntity<WrapperResponse<CourseDTO>> updateCourse(@RequestBody CourseDTO request){
		Course course = courseService.update(courseConvenrter.fromDTO(request));
		WrapperResponse<CourseDTO> response = new WrapperResponse<CourseDTO>(true, "Course updated succesfully.", courseConvenrter.fromEntity(course));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Registrar usuario en un curso */
	@PostMapping("/user/register")
	@Transactional
	public ResponseEntity<WrapperResponse<CourseDTO>> registerUser(@RequestBody RegisterUserOnCourseDTO request){
		courseService.registerUserOnCourse(request.getIdCourse(), request.getIdUser(), request.getRole());
		WrapperResponse<CourseDTO> response = new WrapperResponse<CourseDTO>(true, "User registered succesfully.", null);
        return response.createResponse(HttpStatus.OK);
	}
	
	/* Desasingar usuario de un curso */
	@PostMapping("/user/unregister")
	@Transactional
	public ResponseEntity<WrapperResponse<CourseDTO>> unregisterUser(@RequestBody RegisterUserOnCourseDTO request){
		courseService.unregisterUserOfCourse(request.getIdCourse(), request.getIdUser());
		WrapperResponse<CourseDTO> response = new WrapperResponse<CourseDTO>(true, "User unregistered succesfully.", null);
        return response.createResponse(HttpStatus.OK);
	}
	/* Registrar masiva de usuario a un curso */
	@PostMapping("/multi/user/register")
	@Transactional
	public ResponseEntity<WrapperResponse<CourseDTO>> registerUnregisterMultiUser(
			@RequestParam(name="file", required = true) MultipartFile file,
			@RequestParam(name="unregister", required = false) String unregister){
		courseService.registerUnregisterMultiUser(file, unregister);
		WrapperResponse<CourseDTO> response = new WrapperResponse<CourseDTO>(true,unregister!=null?"Users unbind succesfully." :"Users bind succesfully.", null);
        return response.createResponse(HttpStatus.OK);
	}
	
	/* ##### Calificaciones ##### */
	
	/* Calificar */
	@PostMapping("/calification/create")
	@Transactional
	public ResponseEntity<WrapperResponse<CourseDTO>> saveCalification(@RequestBody SaveCalificationDTO request)
	{
		courseService.saveCalification(calificationConverter.create(request), request.getIdCourse(), request.getIdTeacher(),request.getIdStudent());
		WrapperResponse<CourseDTO> response = new WrapperResponse<CourseDTO>(true,"Calification saved succesfully.", null);
        return response.createResponse(HttpStatus.OK);
	}
	
	/* Multi Califications */
	@PostMapping("/calification/many/create")
	@Transactional
	public ResponseEntity<WrapperResponse<CourseDTO>> saveMultiCalification(
			@RequestParam(name="file", required = true) MultipartFile file,
			@RequestParam(name="idteacher", required = true) Long idTeacher)
	{
		courseService.saveMultiCalification(idTeacher, file);
		WrapperResponse<CourseDTO> response = new WrapperResponse<CourseDTO>(true,"Calification saved succesfully.", null);
        return response.createResponse(HttpStatus.OK);
	}
	
	/* Visualizar calificaciones finales */
	@GetMapping("/calification/view")
	@Transactional
	public ResponseEntity<WrapperResponse<List<CalificationDTO>>> getCalifications(@RequestParam("idStudent") Long idStudent){
		List<Calification> califications = courseService.getCalifications(idStudent);
		WrapperResponse<List<CalificationDTO>> response = new WrapperResponse<List<CalificationDTO>>(true,"Califications listed succesfully.", calificationConverter.fromEntity(califications));
        return response.createResponse(HttpStatus.OK);
	}
	
}
