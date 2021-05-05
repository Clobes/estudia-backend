package com.backend.estudia.converter;

import java.time.format.DateTimeFormatter;

import com.backend.estudia.dto.CalificationDTO;
import com.backend.estudia.dto.SaveCalificationDTO;
import com.backend.estudia.entity.Calification;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CalificationConverter extends AbstractConverter<Calification, CalificationDTO> {

	private DateTimeFormatter dateTimeFormat;
	
	@Override
	public CalificationDTO fromEntity(Calification entity) {
		if(entity == null) return null;
		return CalificationDTO.builder()
				.courseId(entity.getCourse().getId())
				.courseName(entity.getCourse().getName())
				.courseMinimunApproval(entity.getCourse().getMinimunApproval())
				.score(entity.getScore())
				.devolution(entity.getDevolution())
				.creationDate(entity.getCreationDate().format(dateTimeFormat))
				.build();
	}

	@Override
	public Calification fromDTO(CalificationDTO dto) {
		if(dto == null) return null;
		return Calification.builder()
				.score(dto.getScore())
				.devolution(dto.getDevolution())
				.build();
	}
	
	public Calification create(SaveCalificationDTO dto) {
		if(dto == null) return null;
		return Calification.builder()
				.score(dto.getScore())
				.devolution(dto.getDevolution())
				.build();
	}
	
}
