package com.backend.estudia.converter;

import com.backend.estudia.dto.TopicDiscussionSaveDTO;
import com.backend.estudia.dto.TopicDiscussionCustomDTO;
import com.backend.estudia.dto.TopicDiscussionDTO;
import com.backend.estudia.entity.TopicDiscussion;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TopicDiscussionConverter extends AbstractConverter<TopicDiscussion, TopicDiscussionDTO> {

	private UserConverter userConverter;
	private CommentConverter commentConverter;
	
	@Override
	public TopicDiscussionDTO fromEntity(TopicDiscussion entity) {
		if(entity == null) return null;
		return TopicDiscussionDTO.builder()
			.id(entity.getId())
			.title(entity.getTitle())
			.content(entity.getContent())
			.creationDate(entity.getCreationDate())
			.user(userConverter.fromSimpleEntity(entity.getUser()))
			.comments(commentConverter.fromEntity(entity.getComments()))
			.build();
	}

	@Override
	public TopicDiscussion fromDTO(TopicDiscussionDTO dto) {
		if(dto == null) return null;
		return TopicDiscussion.builder()
			.id(dto.getId())
			.title(dto.getTitle())
			.content(dto.getContent())
			.creationDate(dto.getCreationDate())
			.user(userConverter.fromSimpleDTO(dto.getUser()))
			.build();
	}
	
	public TopicDiscussion saveTopicDiscussion(TopicDiscussionSaveDTO dto) {
		if(dto == null) return null;
		TopicDiscussion td = new TopicDiscussion();
		if(dto.getId() != null) {
			td.setId(dto.getId());
		}
		td.setTitle(dto.getTitle());
		td.setContent(dto.getContent());
		return td;
	}
	
	public TopicDiscussionCustomDTO fromEnityToTopicDiscussionCustom(TopicDiscussion entity, Long forumId,
			String forumName, Long courseId, String courseName) {
		if(entity==null) return null;
		return TopicDiscussionCustomDTO.builder()
				.topicDiscussion(fromEntity(entity))
				.forumId(forumId)
				.forumName(forumName)
				.courseId(courseId)
				.courseName(courseName)
				.build();
	}

}
