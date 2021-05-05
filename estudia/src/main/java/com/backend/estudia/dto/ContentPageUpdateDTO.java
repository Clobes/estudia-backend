package com.backend.estudia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ContentPageUpdateDTO {
	
	private Long idContent;
	private Long idUser;
	private Long idCourse;
	private Long idTopic;
	private String title;
	private String content;
	
}
