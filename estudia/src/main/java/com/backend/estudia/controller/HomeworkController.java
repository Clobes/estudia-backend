package com.backend.estudia.controller;

import com.backend.estudia.converter.HomeworkResultConverter;
import com.backend.estudia.converter.LineConverter;
import com.backend.estudia.dto.*;
import com.backend.estudia.entity.*;
import com.backend.estudia.util.WrapperResponseList;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.backend.estudia.converter.CommentConverter;
import com.backend.estudia.converter.HomeworkConverter;
import com.backend.estudia.service.HomeworkService;
import com.backend.estudia.util.WrapperResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/homework")
public class HomeworkController {

    @Autowired
    private HomeworkConverter homeworkConverter;

    @Autowired
    private HomeworkResultConverter homeworkResultConverter;

    @Autowired
    private HomeworkService homeworkService;

    @Autowired
	private CommentConverter commentConverter;

    /* ##### Homeworks ##### */
    
    @GetMapping("/all")
    @Transactional
    public ResponseEntity<WrapperResponseList<List<HomeworkDTO>>> getHomeworks(
            @RequestParam(value = "page", required = false, defaultValue = "0" ) int page,
            @RequestParam(value = "size", required = false, defaultValue = "5" ) int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<Homework> homeworks = homeworkService.getAllHomeworks(pageable);
        long totalElements = homeworks.getTotalElements();
        int totalPages = homeworks.getTotalPages();
        WrapperResponseList<List<HomeworkDTO>> response = new WrapperResponseList<List<HomeworkDTO>>(true, totalElements,
                totalPages, "Homeworks listed succesfully.", homeworkConverter.fromEntity(homeworks.toList()));
        return response.createResponse(HttpStatus.OK);
    }

    /* Listar */
    @GetMapping
    @Transactional
    public ResponseEntity<WrapperResponse<HomeworkDTO>> getHomework(
    		@RequestParam("idCourse") Long idCourse, 
    		@RequestParam("idTopic") Long idTopic, 
    		@RequestParam("idUser") Long idUser,
    		@RequestParam("id") Long id
    	)
    {
        Homework homework = homeworkService.listHomework(idCourse, idTopic, id, idUser);
        WrapperResponse<HomeworkDTO> response = new WrapperResponse<HomeworkDTO>(true, "Homework listed succesfully.", homeworkConverter.fromEntity(homework));
        return response.createResponse(HttpStatus.OK);
    }
    
    /* Crear */
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<WrapperResponse<HomeworkDTO>> createHomework(@RequestBody HomeworkCreateDTO request) {
        Homework homework = homeworkService.create(homeworkConverter.createHomework(request), request.getCourseName(), request.getTopicName(), request.getIdUser());
        WrapperResponse<HomeworkDTO> response = new WrapperResponse<HomeworkDTO>(true, "Homework created succesfully.", homeworkConverter.fromEntity(homework));
        return response.createResponse(HttpStatus.CREATED);
    }
    
    /* Editar */
    @PutMapping("/edit")
    @Transactional
    public ResponseEntity<WrapperResponse<HomeworkDTO>> editHomework(@RequestBody HomeworkEditDTO request) {
        Homework homework = homeworkService.edit(homeworkConverter.editHomework(request), request.getIdCourse(), request.getIdTopic(), request.getIdUser());
        WrapperResponse<HomeworkDTO> response = new WrapperResponse<HomeworkDTO>(true, "Homework saved succesfully.", homeworkConverter.fromEntity(homework));
        return response.createResponse(HttpStatus.OK);
    }
    
    /* Eliminar */
    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<WrapperResponse<HomeworkDTO>> deleteHomework(
    		@RequestParam("idCourse") Long idCourse,
    		@RequestParam("idTopic") Long idTopic,
    		@RequestParam("idUser") Long idUser,
    		@RequestParam("id") Long idHomework
    	) 
    {
    	homeworkService.delete(idCourse, idTopic, idUser, idHomework);
        WrapperResponse<HomeworkDTO> response = new WrapperResponse<HomeworkDTO>(true, "Homework deleted succesfully.", null);
        return response.createResponse(HttpStatus.OK);
    }

    /* ##### Archivos ##### */
    
    /* Info de la tarea*/
    @GetMapping("/file")
    @Transactional
    public ResponseEntity<WrapperResponse<HomeworkResultDTO>> getHomeworkResult(@RequestParam long idCourse, long idTopic, long idHomework, long idUser, long id){
        HomeworkResult homeworkResult = homeworkService.getHomeworkResult(idCourse, idTopic, idHomework, idUser, id);
        WrapperResponse<HomeworkResultDTO> response = new WrapperResponse<HomeworkResultDTO>(true, "File saved succesfully.", homeworkResultConverter.fromEntity(homeworkResult));
        return response.createResponse(HttpStatus.OK);
    }

    /* Descargar */
    @GetMapping("/file/download")
    @Transactional
    public ResponseEntity<Resource> downloadFile(@RequestParam("idCourse") Long idCourse, @RequestParam("idTopic") Long idTopic, @RequestParam("idHomework") Long idHomework, @RequestParam("idUser") Long idUser, @RequestParam("id") Long id){
        HomeworkResult homeworkResult = homeworkService.getHomeworkResult(idCourse, idTopic, idHomework, idUser, id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(homeworkResult.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + homeworkResult.getTitle() + homeworkResult.getExtension() + "\"")
                .body(new ByteArrayResource(homeworkResult.getData()));
    }

    /* Crear */
    @PostMapping("/file/create")
    @Transactional
    public ResponseEntity<WrapperResponse<HomeworkResultDTO>> createFile(
            @RequestParam("idCourse") Long idCourse,
            @RequestParam("idTopic") Long idTopic,
            @RequestParam("idUser") Long idUser,
            @RequestParam("idHomework") Long idHomework,
            @RequestParam("title") String title,
            @RequestParam(value = "file", required = true) MultipartFile file) throws IOException {
        HomeworkResult homeworkResult = homeworkService.createHomeworkResult(idCourse, idTopic, idHomework, idUser, homeworkResultConverter.createHomeworkResultFile(null, title, file));
        WrapperResponse<HomeworkResultDTO> response = new WrapperResponse<HomeworkResultDTO>(true, "File saved succesfully.", homeworkResultConverter.fromEntity(homeworkResult));
        return response.createResponse(HttpStatus.OK);
    }
    
    /* Editar */
    @PutMapping("/file/edit")
    @Transactional
    public ResponseEntity<WrapperResponse<HomeworkResultDTO>> editFile(
            @RequestParam("idCourse") Long idCourse,
            @RequestParam("idTopic") Long idTopic,
            @RequestParam("idUser") Long idUser,
            @RequestParam("idHomework") Long idHomework,
            @RequestParam("title") String title,
            @RequestParam(value = "file", required = true) MultipartFile file) throws IOException {
        HomeworkResult homeworkResult = homeworkService.editHomeworkResult(idCourse, idTopic, idHomework, idUser, homeworkResultConverter.createHomeworkResultFile(null, title, file));
        WrapperResponse<HomeworkResultDTO> response = new WrapperResponse<HomeworkResultDTO>(true, "File saved succesfully.", homeworkResultConverter.fromEntity(homeworkResult));
        return response.createResponse(HttpStatus.OK);
    }

    /* Crear */
    @PostMapping("/page/create")
    @Transactional
    public ResponseEntity<WrapperResponse<HomeworkResultDTO>> createContentPage(@RequestBody HomeworkResultDTO request) throws IOException {
        HomeworkResult homeworkResult = homeworkService.createHomeworkResult(request.getIdCourse(), request.getIdTopic(), request.getIdHomework(), request.getIdUser(), homeworkResultConverter.createHomeworkResultPage(null, request.getContent()));
        WrapperResponse<HomeworkResultDTO> response = new WrapperResponse<HomeworkResultDTO>(true, "Content page created succesfully.", homeworkResultConverter.fromEntity(homeworkResult));
        return response.createResponse(HttpStatus.CREATED);
    }
    
    /* Editar */
    @PutMapping("/page/edit")
    @Transactional
    public ResponseEntity<WrapperResponse<HomeworkResultDTO>> editContentPage(@RequestBody HomeworkResultDTO request) throws IOException {
        HomeworkResult homeworkResult = homeworkService.editHomeworkResult(request.getIdCourse(), request.getIdTopic(), request.getIdHomework(), request.getIdUser(), homeworkResultConverter.createHomeworkResultPage(null, request.getContent()));
        WrapperResponse<HomeworkResultDTO> response = new WrapperResponse<HomeworkResultDTO>(true, "Content page saved succesfully.", homeworkResultConverter.fromEntity(homeworkResult));
        return response.createResponse(HttpStatus.CREATED);
    }

    /* Eliminar */
    /*
    @DeleteMapping("/page/delete")
    public ResponseEntity<WrapperResponse<LineDTO>> deleteContentPage(
            @RequestParam("idCourse") Long idCourse,
            @RequestParam("idTopic") Long idTopic,
            @RequestParam("idUser") Long idUser,
            @RequestParam("id") Long idPage
    )
    {
        contentService.deleteContentPage(idCourse, idTopic, idUser, idPage);
        return new WrapperResponse<LineDTO>(true, "Content page deleted succesfully.", null).createResponse(HttpStatus.OK);
    }

     */
    /* Eliminar */
    /*
    @DeleteMapping("/file/delete")
    public ResponseEntity<WrapperResponse<LineDTO>> deleteFile(@RequestParam("idCourse") Long idCourse, @RequestParam("idTopic") Long idTopic, @RequestParam("idUser") Long idUser, @RequestParam("id") Long id) {
        contentService.deleteFile(idCourse, idTopic, idUser, id);
        WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "File deleted succesfully.", null);
        return response.createResponse(HttpStatus.OK);
    }
     */
    
    /* ##### Comentarios en entregas de tareas ##### */
    
    /* Crear */
	@PostMapping("/homeworkResult/comment/create")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> createCommentHomeworkResult(@RequestBody CommentHomeworkSaveDTO request) {
		Comment comment = homeworkService.saveCommentHomeworkResult(commentConverter.commentHomeworkSave(request), request.getIdCourse(), request.getIdTopic(), request.getIdHomework(), request.getIdHomeworkResult(), request.getIdUser());
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Comment on homework saved succesfully.", commentConverter.fromEntity(comment));
		return response.createResponse(HttpStatus.CREATED);
	}
	
	/* Editar */
	@PutMapping("/homeworkResult/comment/edit")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> editCommentHomeworkResult(@RequestBody CommentHomeworkSaveDTO request) {
		Comment comment = homeworkService.saveCommentHomeworkResult(commentConverter.commentHomeworkSave(request), request.getIdCourse(), request.getIdTopic(), request.getIdHomework(), request.getIdHomeworkResult(), request.getIdUser());
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Comment on homework saved succesfully.", commentConverter.fromEntity(comment));
		return response.createResponse(HttpStatus.CREATED);
	}
	
	/* Eliminar */
	@DeleteMapping("/homeworkResult/comment/delete")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> deleteCommentTopicDiscussion(
			@RequestParam("idCourse") Long idCourse, 
			@RequestParam("idTopic") Long idTopic,
			@RequestParam("idHomework") Long idHomework,
			@RequestParam("idHomeworkResult") Long idHomeworkResult, 
			@RequestParam("idUser") Long idUser, 
			@RequestParam("id") Long id
		)
	{
		homeworkService.deleteCommentHomeworkResult(idCourse, idTopic, idHomework, idHomeworkResult, idUser, id);
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Comment on homework deleted succesfully.", null);
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Crear una respuesta a un comentario */
	@PostMapping("/homeworkResult/comment/response/create")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> createResponseCommentHomeworkResult(@RequestBody CommentResponseSaveDTO request) {
		Comment comment = homeworkService.saveResponseCommentHomeworkResult(commentConverter.commentResponseSave(request), request.getIdCourse(), request.getIdTopic(), request.getIdHomework(), request.getIdHomeworkResult(), request.getIdUser(), request.getIdComment());
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Response on comment saved succesfully.", commentConverter.fromEntity(comment));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Editar una respuesta a un comentario */
	@PutMapping("/homeworkResult/comment/response/edit")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> editResponseCommentHomeworkResult(@RequestBody CommentResponseSaveDTO request) {
		Comment comment = homeworkService.saveResponseCommentHomeworkResult(commentConverter.commentResponseSave(request), request.getIdCourse(), request.getIdTopic(), request.getIdHomework(), request.getIdUser(), request.getIdHomeworkResult(), request.getIdComment());
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Response on comment saved succesfully.", commentConverter.fromEntity(comment));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Verificar si un usuario ya realizó la entrega */
	@GetMapping("/homeworkResult/verify")
	@Transactional
	public ResponseEntity<WrapperResponse<Boolean>> verifyHomeworkResult(
			@RequestParam("idCourse") Long idCourse, 
			@RequestParam("idTopic") Long idTopic, 
			@RequestParam("idHomework") Long idHomework,
			@RequestParam("idUser") Long idUser 
	)
	{
		
		Boolean resultado = homeworkService.verifyHomework(idCourse, idTopic, idHomework, idUser);
		WrapperResponse<Boolean> response = new WrapperResponse<Boolean>(true, "Verificación realizada con éxito", resultado);
		return response.createResponse(HttpStatus.OK);
	}
	
}
