package com.backend.estudia.converter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.estudia.dto.CourseCreateDTO;
import com.backend.estudia.dto.CourseDTO;
import com.backend.estudia.dto.CourseSimpleDTO;
import com.backend.estudia.entity.Course;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CourseConverter extends AbstractConverter<Course, CourseDTO>{
	
	private TopicConverter topicConverter;
	private DateTimeFormatter dateFormat;

	@Override
	public CourseDTO fromEntity(Course entity) {
		if(entity==null) return null;
		return CourseDTO.builder()
				.id(entity.getId())
				.name(entity.getName())
				.area(entity.getArea())
				.year(dateFormat.format(entity.getCreationDate()))
				.description(entity.getDescription())
				.amountHours(entity.getAmountHours())
				.certification(entity.isCertification())
				.minimunApproval(entity.getMinimunApproval())
				.topics(topicConverter.fromEntity(entity.getTopics()))
				.build();
	}

	@Override
	public Course fromDTO(CourseDTO dto) {
		if(dto==null) return null;
		return Course.builder()
				.id(dto.getId())
				.name(dto.getName())
				.area(dto.getArea())
				.description(dto.getDescription())
				.amountHours(dto.getAmountHours())
				.certification(dto.isCertification())
				.minimunApproval(dto.getMinimunApproval())
				.topics(topicConverter.fromDTO(dto.getTopics()))
				.build();
	}
	
	public Course createCourse(CourseCreateDTO courseCreateDto) {
		if(courseCreateDto==null) return null;
		return Course.builder()
				.name(courseCreateDto.getName())
				.area(courseCreateDto.getArea())
				.description(courseCreateDto.getDescription())
				.amountHours(courseCreateDto.getAmountHours())
				.certification(courseCreateDto.isCertification())
				.minimunApproval(courseCreateDto.getMinimunApproval())
				.build();
	}
	
	public CourseSimpleDTO fromSimpleEntity(Course entity) {
		if(entity == null) return null;
		return CourseSimpleDTO.builder()
				.id(entity.getId())
				.name(entity.getName())
				.area(entity.getArea())
				.year(dateFormat.format(entity.getCreationDate()))
				.description(entity.getDescription())
				.amountHours(entity.getAmountHours())
				.certification(entity.isCertification())
				.minimunApproval(entity.getMinimunApproval())
				.build();
	}
	
	public List<CourseSimpleDTO> fromSimpleEntity(List<Course> courses){
		if(courses == null) return null;
		return courses.stream()
				.map(e -> fromSimpleEntity(e))
				.collect(Collectors.toList());
	}
	
}
