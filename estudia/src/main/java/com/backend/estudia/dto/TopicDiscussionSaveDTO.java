package com.backend.estudia.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDiscussionSaveDTO {
	
	private Long idCourse;
	private Long idTopic;
	private Long idForum;
	private Long idUser;
	private Long id;
	private String title;
	private String content;
	private LocalDateTime creationDate;

}
