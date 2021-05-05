package com.backend.estudia.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class HomeworkCreateDTO {

    private String title;
    private String description;
    private String limitDate;
    private String deliveryFormat;
    private String courseName;
    private String topicName;
    private Long idUser;
    
}
