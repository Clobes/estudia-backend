package com.backend.estudia.dto;

import java.util.List;

import com.backend.estudia.util.ForumType;

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
public class ForumDTO extends LineDTO {
	private String description;
	private ForumType forumType;
	private List<TopicDiscussionDTO> topicDiscussions;
}
