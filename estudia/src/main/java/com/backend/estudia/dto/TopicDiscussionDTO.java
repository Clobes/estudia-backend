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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDiscussionDTO {
	private Long id;
	private String title;
	private String content;
	private LocalDateTime creationDate;
	private UserSimpleDTO user;
	private List<CommentDTO> comments;
}
