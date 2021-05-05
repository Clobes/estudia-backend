package com.backend.estudia.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name="topic_discussions")
public class TopicDiscussion implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="title", nullable = false, length = 50)
	private String title;
	@Column(name="content", nullable = false)
	@Lob
	private String content;
	@Column(name="creationDate", nullable = false, length = 150)
	private LocalDateTime creationDate;
	
	@OneToMany
	@JoinColumn(name="topic_disc_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Comment> comments;
	
	@OneToOne
	private User user;
	
	public void setOrderedComments(List<Comment> comments) {
		this.comments.clear();
		if(this.comments != null) {
			this.comments.addAll(comments);
		}
	}
}
