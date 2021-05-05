package com.backend.estudia.dto;

import java.util.List;

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
public class CourseDTO {
	
	private Long id;
	private String name;
	private String year;
	private String area;
	private String description;
	private int amountHours;
	private boolean certification;
	private int minimunApproval;
	private List<TopicDTO> topics;
	
}
