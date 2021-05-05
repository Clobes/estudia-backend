package com.backend.estudia.dto;

import java.util.List;

import com.backend.estudia.util.Roles;

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
public class UserUpdateDTO {

	private Long id;
	private String email;
	private String ci;
	private String firstName;
	private String lastName;
	private String userDescription;
	private String birthDate;
	private String profileImage;
	private List<Roles> roles;
}
