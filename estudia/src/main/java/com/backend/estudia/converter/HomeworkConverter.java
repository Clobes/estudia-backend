package com.backend.estudia.converter;

import com.backend.estudia.dto.*;
import com.backend.estudia.entity.Homework;
import com.backend.estudia.entity.HomeworkResult;
import com.backend.estudia.entity.User;
import com.backend.estudia.repository.IUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
public class HomeworkConverter extends AbstractConverter<Homework, HomeworkDTO>{

    private HomeworkResultConverter homeworkResultConverter;
    private DateTimeFormatter dateFormat;

    @Override
    public HomeworkDTO fromEntity(Homework entity) {
        if(entity==null) return null;
        return HomeworkDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .limitDate(dateFormat.format(entity.getLimitDate()))
                .deliveryFormat(entity.getDeliveryFormat())
                .homeworkResults(homeworkResultConverter.fromEntity(entity.getHomeworkResults()))
                .build();
    }

    @Override
    public Homework fromDTO(HomeworkDTO dto) {
        if(dto==null) return null;

        return Homework.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .limitDate(LocalDateTime.parse(dto.getLimitDate(), dateFormat))
                .deliveryFormat(dto.getDeliveryFormat())
                .homeworkResults(homeworkResultConverter.fromDTO(dto.getHomeworkResults()))
                .build();
    }

    public Homework createHomework(HomeworkCreateDTO dto) {
        if(dto==null) return null;
        List<HomeworkResult> homeworkResults = new ArrayList<HomeworkResult>();
        return Homework.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .limitDate(LocalDateTime.parse(dto.getLimitDate(), dateFormat))
                .deliveryFormat(dto.getDeliveryFormat())
                .homeworkResults(homeworkResults)
                .build();
    }
    
    public Homework editHomework(HomeworkEditDTO dto) {
        if(dto==null) return null;
        return Homework.builder()
        		.id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .limitDate(LocalDateTime.parse(dto.getLimitDate(), dateFormat))
                .deliveryFormat(dto.getDeliveryFormat())
                .build();
    }


}
