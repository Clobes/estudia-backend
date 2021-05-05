package com.backend.estudia.dto;

import org.springframework.web.multipart.MultipartFile;

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
public class FileSaveDTO {

	private Long idCourse;
	private Long idTopic;
	private Long idUser;
	private Long id;
	private String title;
	MultipartFile file;
	
}
