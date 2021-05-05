package com.backend.estudia.converter;

import com.backend.estudia.dto.HomeworkDTO;
import com.backend.estudia.dto.HomeworkResultDTO;
import com.backend.estudia.dto.HomeworkResultFileDTO;
import com.backend.estudia.entity.Homework;
import com.backend.estudia.entity.HomeworkResult;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@AllArgsConstructor
public class HomeworkResultConverter extends AbstractConverter<HomeworkResult, HomeworkResultDTO>{
	
	private CommentConverter commentConverter;
    private DateTimeFormatter dateFormat;

    @Override
    public HomeworkResultDTO fromEntity(HomeworkResult entity) {
        if(entity==null) return null;
        return HomeworkResultDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .deliveryDate(dateFormat.format(entity.getDeliveryDate()))
                .idUser(entity.getUser().getId())
                .userFirstName(entity.getUser().getFirstName())
                .userLastName(entity.getUser().getLastName())
                .ci(entity.getUser().getCi())
                .content(entity.getContent())
                .type(entity.getType())
                .data(entity.getData())
                .comments(commentConverter.fromEntity(entity.getComments()))
                .build();
    }

    @Override
    public HomeworkResult fromDTO(HomeworkResultDTO dto) {
        if(dto==null) return null;

        return HomeworkResult.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .deliveryDate(LocalDateTime.parse(dto.getDeliveryDate(), dateFormat))
                .type(dto.getType())
                .data(dto.getData())
                .build();
    }


    public HomeworkResult createHomeworkResultFile(Long id, String title, MultipartFile file) throws IOException {

        // Obtengo la fecha actual
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date curDate = new Date();
        String dateString = format1.format(curDate);

        return HomeworkResult.builder()
                .id(id)
                .title(title)
                .deliveryDate(LocalDateTime.parse(dateString, dateFormat))
                .type(file.getContentType())
                .extension(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")))
                .data(file.getBytes())
                .build();
    }

    public HomeworkResult createHomeworkResultPage(Long id, String content) throws IOException {

        // Obtengo la fecha actual
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date curDate = new Date();
        String dateString = format1.format(curDate);

        return HomeworkResult.builder()
                .id(id)
                .deliveryDate(LocalDateTime.parse(dateString, dateFormat))
                .content(content)
                .build();
    }
}
