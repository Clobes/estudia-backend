package com.backend.estudia.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

	private Long id;
	private String content;
	private LocalDateTime creationDate;
	private UserSimpleDTO user;
	private List<Long> likes;
	private List<Long> dislikes;
	private List<CommentDTO> responses;
	
}
