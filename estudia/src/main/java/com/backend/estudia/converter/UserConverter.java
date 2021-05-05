package com.backend.estudia.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.backend.estudia.dto.UserCreateDTO;
import com.backend.estudia.dto.UserDTO;
import com.backend.estudia.dto.UserSimpleDTO;
import com.backend.estudia.dto.UserUpdateDTO;
import com.backend.estudia.entity.Role;
import com.backend.estudia.entity.User;
import com.backend.estudia.util.Roles;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserConverter extends AbstractConverter<User, UserDTO>{
	
	private DateTimeFormatter dateFormat;

	@Override
	public UserDTO fromEntity(User entity) {
		if(entity==null) return null;
		return UserDTO.builder()
				.id(entity.getId())
				.ci(entity.getCi())
				.firstName(entity.getFirstName())
				.lastName(entity.getLastName())
				.userDescription(entity.getUserDescription())
				.birthDate(dateFormat.format(entity.getBirthDate()))
				.notificaciones(entity.isNotifications())
				.profileImage(entity.getProfileImage())
				.email(entity.getEmail()!=null?entity.getEmail().toLowerCase():entity.getEmail())
				.roles(entity.getRoles())
				.build();
	}

	@Override
	public User fromDTO(UserDTO dto) {
		if(dto==null) return null;
		return User.builder()
				.id(dto.getId())
				.ci(dto.getCi())
				.firstName(dto.getFirstName())
				.lastName(dto.getLastName())
				.userDescription(dto.getUserDescription())
				.birthDate(LocalDate.parse(dto.getBirthDate(), dateFormat))
				.profileImage(dto.getProfileImage())
				.notifications(dto.getNotificaciones())
				.email(dto.getEmail()!=null?dto.getEmail().toLowerCase():dto.getEmail())
				.roles(dto.getRoles())
				.build();
	}
	
	public User createUser(UserCreateDTO userCreateDto) {
		if(userCreateDto==null) return null;
		return User.builder()
				.ci(userCreateDto.getCi())
				.firstName(userCreateDto.getFirstName())
				.lastName(userCreateDto.getLastName())
				.userDescription(userCreateDto.getUserDescription())
				.birthDate(LocalDate.parse(userCreateDto.getBirthDate(), dateFormat))
				.email(userCreateDto.getEmail()!=null?userCreateDto.getEmail().toLowerCase():userCreateDto.getEmail())
				.password(userCreateDto.getPassword())
				.profileImage(userCreateDto.getProfileImage())
				.roles(convertEnumRolesToEntityRol(userCreateDto.getRoles()))
				.build();
	}
	public User updateUser(UserUpdateDTO userUpdateDto) {
		if(userUpdateDto==null) return null;
		return User.builder()
				.id(userUpdateDto.getId())
				.ci(userUpdateDto.getCi())
				.firstName(userUpdateDto.getFirstName())
				.lastName(userUpdateDto.getLastName())
				.userDescription(userUpdateDto.getUserDescription())
				.birthDate(LocalDate.parse(userUpdateDto.getBirthDate(), dateFormat))
				.email(userUpdateDto.getEmail()!=null?userUpdateDto.getEmail().toLowerCase():userUpdateDto.getEmail())
				.profileImage(userUpdateDto.getProfileImage())
				.roles(convertEnumRolesToEntityRol(userUpdateDto.getRoles()))
				.build();
	}
	
	public UserDTO FromEntityWithProfileImg(User entity) {
		if(entity==null) return null;
		UserDTO dto = fromEntity(entity);
		dto.setProfileImage(entity.getProfileImage());
		return dto;
	}
	
	private List<Role> convertEnumRolesToEntityRol(List<Roles> roles) {
		if(roles==null) return null;
		List<Role> listRoles = new ArrayList<Role>();
		for (Roles r : roles) {
			Role newRol = new Role();
			newRol.setName(r.name().toString());
			listRoles.add(newRol);
		}
		return listRoles;
	}
	
	public UserSimpleDTO fromSimpleEntity(User entity) {
		if(entity == null) return null;
		return UserSimpleDTO.builder()
				.id(entity.getId())
				.firstName(entity.getFirstName())
				.profileImage(entity.getProfileImage())
				.lastName(entity.getLastName())
				.build();
	}
	
	public User fromSimpleDTO(UserSimpleDTO dto) {
		if(dto == null) return null;
		return User.builder()
				.id(dto.getId())
				.firstName(dto.getFirstName())
				.lastName(dto.getLastName())
				.build();
	}

}
