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
public class CourseCreateDTO {

	private String name;
	private String area;
	private String description;
	private int amountHours;
	private boolean certification;
	private int minimunApproval;
	
}
