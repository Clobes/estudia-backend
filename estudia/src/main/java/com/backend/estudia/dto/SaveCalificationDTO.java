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
public class SaveCalificationDTO {

	private Long idCourse; 
	private Long idTeacher; 
	private Long idStudent; 
	private Double score;
	private String devolution;
	
}
