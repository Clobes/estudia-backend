package com.backend.estudia.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
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
@Builder
@Entity
@Table(name="comments")
public class Comment implements Serializable {
	
	/* Attributes */
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", nullable=false)
	private Long id;
	@Column(name="content", nullable=false, length=500)
	private String content;
	@Column(name="creationDate", nullable=false)
	private LocalDateTime creationDate;
	
	/* Relationships */
	@OneToOne
	private User user;
	
	/* Me gusta */
	@ManyToMany
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinTable(name = "comment_likes", joinColumns = {@JoinColumn(name="comment_id")}, inverseJoinColumns = {@JoinColumn(name="user_id")})
	private List<User> likes;
	
	/* No me gusta */
	@ManyToMany
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinTable(name = "comment_dislikes", joinColumns = {@JoinColumn(name="comment_id")}, inverseJoinColumns = {@JoinColumn(name="user_id")})
	private List<User> dislikes;
	
	@ManyToOne
	private Comment parent_comment;
	
	/* Respuestas */
	@OneToMany(mappedBy = "parent_comment")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Comment> responses;
	
}
