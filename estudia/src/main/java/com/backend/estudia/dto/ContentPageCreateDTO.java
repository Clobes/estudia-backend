package com.backend.estudia.dto;

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
public class ContentPageCreateDTO {
	private Long idUser;
	private Long idCourse;
	private Long idTopic;
	private String title;
	private String content;
}
