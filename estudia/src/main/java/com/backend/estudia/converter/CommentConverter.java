package com.backend.estudia.converter;

import com.backend.estudia.dto.CommentSaveDTO;

import java.util.ArrayList;
import java.util.List;

import com.backend.estudia.dto.CommentDTO;
import com.backend.estudia.dto.CommentHomeworkSaveDTO;
import com.backend.estudia.dto.CommentResponseSaveDTO;
import com.backend.estudia.entity.Comment;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentConverter extends AbstractConverter<Comment, CommentDTO> {

	private UserConverter userConverter;
	
	@Override
	public CommentDTO fromEntity(Comment entity) {
		if(entity == null) return null;
		List<Long> likes = new ArrayList<>();
		List<Long> dislikes = new ArrayList<>();
		if(entity.getLikes() != null) {
			entity.getLikes().forEach(l -> likes.add(l.getId()));
		}
		if(entity.getDislikes() != null) {
			entity.getDislikes().forEach(d -> dislikes.add(d.getId()));
		}
		return CommentDTO.builder()
				.id(entity.getId())
				.content(entity.getContent())
				.creationDate(entity.getCreationDate())
				.user(userConverter.fromSimpleEntity(entity.getUser()))
				.likes(likes)
				.dislikes(dislikes)
				.responses(this.fromEntity(entity.getResponses()))
				.build();
	}

	@Override
	public Comment fromDTO(CommentDTO dto) {
		if(dto == null) return null;
		return Comment.builder()
				.id(dto.getId())
				.content(dto.getContent())
				.creationDate(dto.getCreationDate())
				.user(userConverter.fromSimpleDTO(dto.getUser()))
				.build();
	}
	
	public Comment commentSave(CommentSaveDTO dto) {
		if(dto == null) return null;
		Comment c = new Comment();
		if(dto.getId() != null) {
			c.setId(dto.getId());
		}
		c.setContent(dto.getContent());
		return c;
	}
	
	public Comment commentResponseSave(CommentResponseSaveDTO dto) {
		if(dto == null) return null;
		Comment c = new Comment();
		if(dto.getId() != null) {
			c.setId(dto.getId());
		}
		c.setContent(dto.getContent());
		return c;
	}
	
	public Comment commentHomeworkSave(CommentHomeworkSaveDTO dto) {
		if(dto == null) return null;
		Comment c = new Comment();
		if(dto.getId() != null) {
			c.setId(dto.getId());
		}
		c.setContent(dto.getContent());
		return c;
	}
	
}
