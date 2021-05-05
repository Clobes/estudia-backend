package com.backend.estudia.dto;

import lombok.Setter;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentSaveDTO {

	private Long idCourse;
	private Long idTopic;
	private Long idForum;
	private Long idTopicDiscussion;
	private Long idUser;
	private Long id;
	private String content;
	
}
