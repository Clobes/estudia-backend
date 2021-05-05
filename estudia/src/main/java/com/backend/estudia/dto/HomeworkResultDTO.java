package com.backend.estudia.dto;

import java.util.List;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class HomeworkResultDTO {
	
    private Long id;
    private Long idCourse;
    private Long idTopic;
    private Long idHomework;
    private String title;
    private String deliveryDate;
    private String content;
    private String type;
    private byte[] data;
    private Long idUser;
    private String userFirstName;
    private String userLastName;
    private String ci;
    private List<CommentDTO> comments;
}
