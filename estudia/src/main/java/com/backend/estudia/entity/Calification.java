package com.backend.estudia.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "califications")
public class Calification {

	@EmbeddedId
	private CalificationID id = new CalificationID();
	
	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@MapsId("courseId")
	@JoinColumn(name = "course_id")
	private Course course;
	
	@Column(name = "score", nullable = false)
	private Double score;
	
	@Column(name = "devolution", nullable = false, length = 500)
	private String devolution;
	
	@Column(name = "creationDate", nullable = false)
	private LocalDateTime creationDate;
	
}
