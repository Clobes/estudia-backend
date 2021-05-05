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
public class HomeworkEditDTO {

	private Long idCourse;
	private Long idTopic;
	private Long idUser;
	private Long id;
    private String title;
    private String description;
    private String limitDate;
    private String deliveryFormat;
    
}
