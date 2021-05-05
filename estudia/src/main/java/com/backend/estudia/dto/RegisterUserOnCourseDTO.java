package com.backend.estudia.dto;

import com.backend.estudia.util.Roles;

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
public class RegisterUserOnCourseDTO {

	private Long idCourse;
	private Long idUser;
	private Roles role;
	
}
