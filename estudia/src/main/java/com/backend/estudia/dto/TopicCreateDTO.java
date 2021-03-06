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
public class TopicCreateDTO {
	
	private Long idCourse;
	private Long idUser;
	private Long id;
	private String name;
	private String description;
	
}
