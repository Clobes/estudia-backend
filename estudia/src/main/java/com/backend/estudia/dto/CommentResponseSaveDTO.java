package com.backend.estudia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseSaveDTO {
	
	private Long idCourse;
	private Long idTopic;
	private Long idForum;
	private Long idTopicDiscussion;
	private Long idHomework;
	private Long idHomeworkResult;
	private Long idUser;
	private Long idComment;
	private Long id;
	private String content;
	
}
