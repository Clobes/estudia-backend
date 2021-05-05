package com.backend.estudia.dto;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentHomeworkSaveDTO {

	private Long idCourse;
	private Long idTopic;
	private Long idForum;
	private Long idHomework;
	private Long idHomeworkResult;
	private Long idUser;
	private Long id;
	private String content;
	
}
