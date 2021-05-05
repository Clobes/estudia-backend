package com.backend.estudia.validator;

import com.backend.estudia.entity.Comment;
import com.backend.estudia.entity.ContentPage;
import com.backend.estudia.entity.File;
import com.backend.estudia.entity.Forum;
import com.backend.estudia.entity.Topic;
import com.backend.estudia.entity.TopicDiscussion;
import com.backend.estudia.exception.ValidateServiceException;

public class ContentValidator {
	
	public static void topicCreate(Topic topic) {
				
		if(topic.getName() == null || topic.getName().trim().isEmpty()) {
			throw new ValidateServiceException("The name field is required.");
		}	
		
	}
	
	public static void pageContentCreate(ContentPage contentPage, Long idCourse, Long idTopic, Long idUser) {
		
		if(contentPage.getTitle() == null || contentPage.getTitle().trim().isEmpty()) {
			throw new ValidateServiceException("The title field is required.");
		}	
		if(contentPage.getContent()== null || contentPage.getContent().trim().isEmpty()) {
			throw new ValidateServiceException("The content field is required.");
		}
		if(idCourse== null) {
			throw new ValidateServiceException("The id course field is required.");
		}
		if(idTopic== null) {
			throw new ValidateServiceException("The id topic field is required.");
		}
		if(idUser== null) {
			throw new ValidateServiceException("The id user field is required.");
		}
	}
	
	public static void pageContentUpdate(ContentPage contentPage, Long idCourse, Long idTopic, Long idUser) {
		
		if(contentPage.getTitle() == null || contentPage.getTitle().trim().isEmpty()) {
			throw new ValidateServiceException("The title field is required.");
		}	
		if(contentPage.getContent()== null || contentPage.getContent().trim().isEmpty()) {
			throw new ValidateServiceException("The content field is required.");
		}
		if(idCourse== null) {
			throw new ValidateServiceException("The course id field is required.");
		}
		if(idTopic== null) {
			throw new ValidateServiceException("The topic id field is required.");
		}
		if(idUser== null) {
			throw new ValidateServiceException("The user id field is required.");
		}
		if(contentPage.getId() == null) {
			throw new ValidateServiceException("The content page id field is required.");
		}
		
	}

	public static void fileCreate(File file) {
		if(file.getTitle() == null || file.getTitle().trim().isEmpty()) 
			throw new ValidateServiceException("The title field is required.");
	}
	
	public static void forumCreate(Forum forum) {
		if(forum.getTitle() == null || forum.getTitle().trim().isEmpty())
			throw new ValidateServiceException("The title filed is required.");
		if(forum.getDescription() == null || forum.getDescription().trim().isEmpty())
			throw new ValidateServiceException("The description filed is required.");
		if(forum.getForumType() == null)
			throw new ValidateServiceException("The forum type filed is required.");
	}
	
	public static void topicDiscussionCreate(TopicDiscussion topicDisc) {
		if(topicDisc.getTitle() == null || topicDisc.getTitle().trim().isEmpty())
			throw new ValidateServiceException("The title field is requiered.");
		if(topicDisc.getContent() == null || topicDisc.getContent().trim().isEmpty())
			throw new ValidateServiceException("The content field is required.");
	}
	
	public static void commentTopicDiscussionCreate(Comment comment) {
		if(comment.getContent() == null || comment.getContent().trim().isEmpty())
			throw new ValidateServiceException("The content field is required.");
	}

}
