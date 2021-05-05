package com.backend.estudia.validator;

import com.backend.estudia.entity.Calification;
import com.backend.estudia.entity.Course;
import com.backend.estudia.exception.ValidateServiceException;

public class CourseValidator {
	
	public static void create(Course course) {
				
		if(course.getName() == null || course.getName().trim().isEmpty()) {
			throw new ValidateServiceException("The name field is required.");
		}	
		if(course.getArea() == null || course.getArea().trim().isEmpty()) {
			throw new ValidateServiceException("The area field is required.");
		}
		if(course.getDescription()== null || course.getDescription().trim().isEmpty()) {
			throw new ValidateServiceException("The description field is required.");
		}
	}
	
	public static void update(Course course) {

		if(course.getId() == null || course.getName().trim().isEmpty()) {
			throw new ValidateServiceException("The id field is required.");
		}
		if(course.getName() == null || course.getName().trim().isEmpty()) {
			throw new ValidateServiceException("The name field is required.");
		}	
		if(course.getArea() == null || course.getArea().trim().isEmpty()) {
			throw new ValidateServiceException("The area field is required.");
		}
		if(course.getDescription()== null || course.getDescription().trim().isEmpty()) {
			throw new ValidateServiceException("The description field is required.");
		}
		if(course.getAmountHours() < 1 ) {
			throw new ValidateServiceException("The amount hours is not valid.");
		}
		if(course.getMinimunApproval() < 1 ) {
			throw new ValidateServiceException("The minumun approval is not valid.");
		}
		
	}
	
	public static void calificationCreate(Calification calification) {
		if(calification.getScore() == null || calification.getScore() < 0)
			throw new ValidateServiceException("Calification must be a positive number.");
	}

}
