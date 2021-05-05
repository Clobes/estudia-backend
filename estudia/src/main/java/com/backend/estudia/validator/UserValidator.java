package com.backend.estudia.validator;

import com.backend.estudia.entity.User;
import com.backend.estudia.exception.ValidateServiceException;
import com.backend.estudia.util.Kte;

public class UserValidator {
	
	public static void create(User user) {
		
		if(user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			throw new ValidateServiceException("The email field is required.");
		}
		
		if(!user.getEmail().trim().matches(Kte.pattern_email)) {
			throw new ValidateServiceException("The email is not valid.");
		}		
		
		if(user.getCi() == null || user.getCi().trim().isEmpty()) {
			throw new ValidateServiceException("The ci field is required.");
		}
		
		if(user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
			throw new ValidateServiceException("The firstname field is required.");
		}
		
		if(user.getLastName() == null || user.getLastName().trim().isEmpty()) {
			throw new ValidateServiceException("The lastname field is required.");
		}
		if(user.getRoles() == null || user.getRoles().isEmpty()) {
			throw new ValidateServiceException("The rol field is required.");
		}
	}

}
