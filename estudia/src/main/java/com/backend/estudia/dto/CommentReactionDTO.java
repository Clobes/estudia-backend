package com.backend.estudia.dto;

import com.backend.estudia.util.Reaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentReactionDTO {
	
	private Long idCourse;
	private Long idTopic;
	private Long idForum;
	private Long idTopicDiscussion;
	private Long idUser;
	private Long id;
	private Reaction reaction;
	
}
