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
public class CalificationDTO {
	
	private Long courseId;
	private String courseName;
	private int courseMinimunApproval;
	private Double score;
	private String devolution;
	private String creationDate;
	
}
