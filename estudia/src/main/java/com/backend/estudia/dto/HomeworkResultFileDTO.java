package com.backend.estudia.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class HomeworkResultFileDTO {
    private Long id;
    private String title;
    private String deliveryDate;
    private String type;
    private byte[] data;
    private Long idUser;
}
