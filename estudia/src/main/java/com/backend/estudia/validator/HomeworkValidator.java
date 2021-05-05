package com.backend.estudia.validator;

import com.backend.estudia.entity.Course;
import com.backend.estudia.entity.Homework;
import com.backend.estudia.exception.ValidateServiceException;

public class HomeworkValidator {

    public static void create(Homework homework) {

        if(homework.getTitle() == null || homework.getTitle().trim().isEmpty()) {
            throw new ValidateServiceException("The title field is required.");
        }
        if(homework.getDeliveryFormat() == null || homework.getDeliveryFormat().trim().isEmpty()) {
            throw new ValidateServiceException("The delivery format field is required.");
        }
    }
}
