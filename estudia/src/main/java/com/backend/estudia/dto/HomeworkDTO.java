package com.backend.estudia.dto;

import com.backend.estudia.entity.HomeworkResult;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class HomeworkDTO {

    private Long id;
    private String title;
    private String description;
    private String limitDate;
    private String deliveryFormat;
    private Long idUser;
    private List<HomeworkResultDTO> homeworkResults;
}
