package com.backend.estudia.dto;

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
public class TopicDiscussionCustomDTO {
	
	private TopicDiscussionDTO topicDiscussion;
	private Long forumId;
	private String forumName;
	private Long courseId;
	private String courseName;

}
