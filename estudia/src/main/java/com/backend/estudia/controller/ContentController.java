package com.backend.estudia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import com.backend.estudia.converter.CommentConverter;
import com.backend.estudia.converter.LineConverter;
import com.backend.estudia.converter.TopicConverter;
import com.backend.estudia.converter.TopicDiscussionConverter;
import com.backend.estudia.dto.*;
import com.backend.estudia.entity.Comment;
import com.backend.estudia.entity.ContentPage;
import com.backend.estudia.entity.File;
import com.backend.estudia.entity.Forum;
import com.backend.estudia.entity.Topic;
import com.backend.estudia.entity.TopicDiscussion;
import com.backend.estudia.service.ContentService;
import com.backend.estudia.util.WrapperResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/content")
public class ContentController {
	
	@Autowired
	private TopicConverter topicConvenrter;
	
	@Autowired
	private LineConverter lineConverter;
	
	@Autowired
	private TopicDiscussionConverter topicDiscConverter;
	
	@Autowired
	private CommentConverter commentConverter;
	
	@Autowired
	private ContentService contentService;
	
	/* ##### Temas  ##### */
	
	/* Crear */
	@PostMapping("/topic/create")
	@Transactional
	public ResponseEntity<WrapperResponse<TopicDTO>> createTopic(@RequestBody TopicCreateDTO request) {
		Topic topic = contentService.saveTopic(topicConvenrter.createTopic(request), request.getIdCourse(), request.getIdUser());
		WrapperResponse<TopicDTO> response = new WrapperResponse<TopicDTO>(true, "Topic created succesfully.", topicConvenrter.fromEntity(topic));
		return response.createResponse(HttpStatus.CREATED);
	}
	
	/* Editar */
	@PutMapping("/topic/edit")
	@Transactional
	public ResponseEntity<WrapperResponse<TopicDTO>> editTopic(@RequestBody TopicCreateDTO request) {
		Topic topic = contentService.saveTopic(topicConvenrter.createTopic(request), request.getIdCourse(), request.getIdUser());
		WrapperResponse<TopicDTO> response = new WrapperResponse<TopicDTO>(true, "Topic saved succesfully.", topicConvenrter.fromEntity(topic));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Eliminar */
	@DeleteMapping("/topic/delete")
	@Transactional
	public ResponseEntity<WrapperResponse<TopicDTO>> deleteTopic(
			@RequestParam("idCourse") Long idCourse,
			@RequestParam("idUser") Long idUser,
			@RequestParam("id") Long idTopic
		) 
	{
		contentService.deleteTopic(idCourse, idUser, idTopic);
		WrapperResponse<TopicDTO> response = new WrapperResponse<TopicDTO>(true, "Topic deleted succesfully.", null);
		return response.createResponse(HttpStatus.OK);
	}
	
	/* ##### Páginas de contenido ##### */
	
	/* Listar por id */
	@GetMapping("/page/list")
	@Transactional
	public ResponseEntity<WrapperResponse<LineDTO>> getContentPage(
			@RequestParam("idCourse") Long idCourse,
			@RequestParam("idTopic") Long idTopic,
			@RequestParam("idUser") Long idUser,
			@RequestParam("id") Long idPage
		) 
	{
		ContentPage content = contentService.getContentPageById(idCourse, idTopic, idUser, idPage);
		WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "Content page listed succesfully.", lineConverter.fromEntity(content));
		return response.createResponse(HttpStatus.CREATED);
	}
	
	/* Crear */
	@PostMapping("/page/create")
	@Transactional
	public ResponseEntity<WrapperResponse<LineDTO>> createContentPage(@RequestBody ContentPageCreateDTO request) {
		ContentPage content = contentService.contentPageCreate(lineConverter.createContentPage(request), request.getIdCourse(), request.getIdTopic(), request.getIdUser());
		WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "Content page created succesfully.", lineConverter.fromEntity(content));
		return response.createResponse(HttpStatus.CREATED);
	}
	
	/* Editar */
	@PutMapping("/page/edit")
	@Transactional
	public ResponseEntity<WrapperResponse<LineDTO>> updateContentPage(@RequestBody ContentPageUpdateDTO request){
		ContentPage content = contentService.contentPageUpdate(lineConverter.updateContentPage(request), request.getIdCourse(), request.getIdTopic(),  request.getIdUser());
		WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "Content page updated succesfully.", lineConverter.fromEntity(content));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Eliminar */
	@DeleteMapping("/page/delete")
	@Transactional
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
			
	/* ##### Archivos ##### */
	
	/* Descargar */
	@GetMapping("/file/download")
	@Transactional
	public ResponseEntity<Resource> downloadFile(
			@RequestParam("idCourse") Long idCourse, 
			@RequestParam("idTopic") Long idTopic, 
			@RequestParam("idUser") Long idUser, 
			@RequestParam("id") Long id
		)
	{
		File file = contentService.downloadFile(idCourse, idTopic, idUser, id);
		return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getTitle() + file.getExtension() + "\"")
                .body(new ByteArrayResource(file.getData()));
	}
	
	/* Crear */
	@PostMapping("/file/create")
	@Transactional
	public ResponseEntity<WrapperResponse<LineDTO>> createFile(
			@RequestParam("idCourse") Long idCourse,
			@RequestParam("idTopic") Long idTopic,
			@RequestParam("idUser") Long idUser,
			@RequestParam("title") String title,
			@RequestParam(value = "file", required = true) MultipartFile file
		) 
	{
		File newfile = contentService.saveFile(idCourse, idTopic, idUser, lineConverter.saveFile(null, title, file));
		WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "File saved succesfully.", lineConverter.fromEntity(newfile));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Editar */
	@PutMapping("/file/edit")
	@Transactional
	public ResponseEntity<WrapperResponse<LineDTO>> editFile(
			@RequestParam("idCourse") Long idCourse,
			@RequestParam("idTopic") Long idTopic,
			@RequestParam("idUser") Long idUser,
			@RequestParam("id") Long id,
			@RequestParam("title") String title,
			@RequestParam(value = "file", required = true) MultipartFile file
		) 
	{
		File newfile = contentService.saveFile(idCourse, idTopic, idUser, lineConverter.saveFile(id, title, file));
		WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "File saved succesfully.", lineConverter.fromEntity(newfile));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Eliminar */
	@DeleteMapping("/file/delete")
	@Transactional
	public ResponseEntity<WrapperResponse<LineDTO>> deleteFile(@RequestParam("idCourse") Long idCourse, @RequestParam("idTopic") Long idTopic, @RequestParam("idUser") Long idUser, @RequestParam("id") Long id) {
		contentService.deleteFile(idCourse, idTopic, idUser, id);
		WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "File deleted succesfully.", null);
		return response.createResponse(HttpStatus.OK);
	}
	
	/* ##### Foros ##### */

	/* Listar */

	@GetMapping("/forum/list")
	@Transactional
	public ResponseEntity<WrapperResponse<LineCustomDTO>> listForum(
			@RequestParam("idCourse") Long idCourse,
			@RequestParam("idTopic") Long idTopic,
			@RequestParam("idUser") Long idUser,
			@RequestParam("id") Long idForum
		) 
	{
		LineCustomDTO lineCustom = contentService.listForum(idCourse, idTopic, idUser, idForum);
		WrapperResponse<LineCustomDTO> response = new WrapperResponse<LineCustomDTO>(true, "Forum listed succesfully.", lineCustom);
		return response.createResponse(HttpStatus.OK);
	}

	/* Crear */
	@PostMapping("/forum/create")
	@Transactional
	public ResponseEntity<WrapperResponse<LineDTO>> createForum(@RequestBody ForumSaveDTO request) {
		Forum forum = contentService.saveForum(lineConverter.saveForum(request), request.getIdCourse(), request.getIdTopic(), request.getIdUser());
		WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "Forum saved succesfully.", lineConverter.fromEntity(forum));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Editar */
	@PutMapping("/forum/edit")
	@Transactional
	public ResponseEntity<WrapperResponse<LineDTO>> editForum(@RequestBody ForumSaveDTO request) {
		Forum forum = contentService.saveForum(lineConverter.saveForum(request), request.getIdCourse(), request.getIdTopic(), request.getIdUser());
		WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "Forum saved succesfully.", lineConverter.fromEntity(forum));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Eliminar */
	@DeleteMapping("/forum/delete")
	@Transactional
	public ResponseEntity<WrapperResponse<LineDTO>> deleteForum(
			@RequestParam("idCourse") Long idCourse,
			@RequestParam("idTopic") Long idTopic,
			@RequestParam("idUser") Long idUser,
			@RequestParam("id") Long idForum
		) 
	
	{
		contentService.deleteForum(idCourse, idTopic, idUser, idForum);
		WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "Forum deleted succesfully.", null);
		return response.createResponse(HttpStatus.OK);
	}
	
	/* ##### Temas de discusión ##### */
	
	/* Listar */
	@GetMapping("/topic/discussion/list")
	@Transactional
	public ResponseEntity<WrapperResponse<TopicDiscussionCustomDTO>> listTopicDiscussion(
			@RequestParam("idCourse") Long idCourse,
			@RequestParam("idTopic") Long idTopic,
			@RequestParam("idForum") Long idForum,
			@RequestParam("idUser") Long idUser, 
			@RequestParam("id") Long id
		)
	{
		TopicDiscussionCustomDTO td = contentService.listTopicDiscussion(idCourse, idTopic, idForum, idUser, id);
		WrapperResponse<TopicDiscussionCustomDTO> response = new WrapperResponse<TopicDiscussionCustomDTO>(true, "Topic discussion listed succesfully.", td);
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Crear */
	@PostMapping("/topic/discussion/create")
	@Transactional
	public ResponseEntity<WrapperResponse<TopicDiscussionDTO>> createTopicDiscussion(@RequestBody TopicDiscussionSaveDTO request) {
		TopicDiscussion td = contentService.saveTopicDiscussion(topicDiscConverter.saveTopicDiscussion(request), request.getIdCourse(), request.getIdTopic(), request.getIdForum(), request.getIdUser());
		WrapperResponse<TopicDiscussionDTO> response = new WrapperResponse<TopicDiscussionDTO>(true, "Topic discussion saved succesfully.", topicDiscConverter.fromEntity(td));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Editar */
	@PutMapping("/topic/discussion/edit")
	@Transactional
	public ResponseEntity<WrapperResponse<TopicDiscussionDTO>> editTopicDiscussion(@RequestBody TopicDiscussionSaveDTO request) {
		TopicDiscussion td = contentService.saveTopicDiscussion(topicDiscConverter.saveTopicDiscussion(request), request.getIdCourse(), request.getIdTopic(), request.getIdForum(), request.getIdUser());
		WrapperResponse<TopicDiscussionDTO> response = new WrapperResponse<TopicDiscussionDTO>(true, "Topic discussion saved succesfully.", topicDiscConverter.fromEntity(td));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Eliminar */
	@DeleteMapping("/topic/discussion/delete")
	@Transactional
	public ResponseEntity<WrapperResponse<LineDTO>> deleteTopicDiscussion(
			@RequestParam("idCourse") Long idCourse,
			@RequestParam("idTopic") Long idTopic,
			@RequestParam("idForum") Long idForum,
			@RequestParam("idUser") Long idUser, 
			@RequestParam("id") Long id
		)
	{
		contentService.deleteTopicDiscussion(idCourse, idTopic, idForum, idUser, id);
		WrapperResponse<LineDTO> response = new WrapperResponse<LineDTO>(true, "Topic discussion deleted succesfully.", null);
		return response.createResponse(HttpStatus.OK);
	}
	
	/* ##### Comentarios en temas de discusión ##### */
	
	/* Crear */
	@PostMapping("/topic/discussion/comment/create")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> createCommentTopicDiscussion(@RequestBody CommentSaveDTO request) {
		Comment comment = contentService.saveCommentTopicDiscussion(commentConverter.commentSave(request), request.getIdCourse(), request.getIdTopic(), request.getIdForum(), request.getIdTopicDiscussion(), request.getIdUser());
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Comment on topic discussion saved succesfully.", commentConverter.fromEntity(comment));
		return response.createResponse(HttpStatus.CREATED);
	}
	
	/* Editar */
	@PutMapping("/topic/discussion/comment/edit")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> editCommentTopicDiscussion(@RequestBody CommentSaveDTO request) {
		Comment comment = contentService.saveCommentTopicDiscussion(commentConverter.commentSave(request), request.getIdCourse(), request.getIdTopic(), request.getIdForum(), request.getIdTopicDiscussion(), request.getIdUser());
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Comment on topic discussion saved succesfully.", commentConverter.fromEntity(comment));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Eliminar */
	@DeleteMapping("/topic/discussion/comment/delete")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> deleteCommentTopicDiscussion(
			@RequestParam("idCourse") Long idCourse,
			@RequestParam("idTopic") Long idTopic,
			@RequestParam("idForum") Long idForum,
			@RequestParam("idTopicDiscussion") Long idTopicDiscussion,
			@RequestParam("idUser") Long idUser, 
			@RequestParam("id") Long id
		)
	{
		contentService.deleteCommentTopicDiscussion(idCourse, idTopic, idForum, idTopicDiscussion, idUser, id);
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Comment on topic discussion deleted succesfully.", null);
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Crear una respuesta a un comentario */
	@PostMapping("/topic/discussion/comment/response/create")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> createResponseCommentTopicDiscussion(@RequestBody CommentResponseSaveDTO request) {
		Comment comment = contentService.saveResponseCommentTopicDiscussion(commentConverter.commentResponseSave(request), request.getIdCourse(), request.getIdTopic(), request.getIdForum(), request.getIdTopicDiscussion(), request.getIdUser(), request.getIdComment());
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Response on comment saved succesfully.", commentConverter.fromEntity(comment));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* Editar una respuesta a un comentario */
	@PutMapping("/topic/discussion/comment/response/edit")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> editResponseCommentTopicDiscussion(@RequestBody CommentResponseSaveDTO request) {
		Comment comment = contentService.saveResponseCommentTopicDiscussion(commentConverter.commentResponseSave(request), request.getIdCourse(), request.getIdTopic(), request.getIdForum(), request.getIdTopicDiscussion(), request.getIdUser(), request.getIdComment());
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Response on comment saved succesfully.", commentConverter.fromEntity(comment));
		return response.createResponse(HttpStatus.OK);
	}
	
	/* ##### Reacciones ##### */
	
	/* Reaccionar */
	@PostMapping("/topic/discussion/comment/reaction")
	@Transactional
	public ResponseEntity<WrapperResponse<CommentDTO>> likeCommentTopicDiscussion(@RequestBody CommentReactionDTO request) {
		Comment comment = contentService.reactionComment(request.getIdCourse(), request.getIdTopic(), request.getIdForum(), request.getIdTopicDiscussion(), request.getIdUser(), request.getId(), request.getReaction());
		WrapperResponse<CommentDTO> response = new WrapperResponse<CommentDTO>(true, "Reaction saved succesfully.", commentConverter.fromEntity(comment));
		return response.createResponse(HttpStatus.OK);
	}
	
}
