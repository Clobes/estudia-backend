package com.backend.estudia.dto;

import java.util.List;

import com.backend.estudia.entity.Role;

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
public class UserDTO {

	private Long id;
	private String email;
	private String ci;
	private String firstName;
	private String lastName;
	private String userDescription;
	private String birthDate;
	private String profileImage;
	private List<Role> roles;
	private Boolean notificaciones;
	private String tokenNotifiacion;

}
