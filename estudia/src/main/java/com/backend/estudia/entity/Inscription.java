package com.backend.estudia.entity;

import java.time.LocalDate;

import javax.persistence.*;

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
@Table(name="inscriptions")
public class Inscription {
	
	@EmbeddedId
	private InscriptionID id = new InscriptionID();
	
	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToOne
	@MapsId("courseId")
	@JoinColumn(name="course_id")
	private Course course;
	
	@Column(name="endDate", nullable=true)
	private LocalDate endDate;
	
	@Column(name="score", nullable=true)
	private int score;
	
	@Column(name="devolution", nullable=true, length=255)
	private String devolution;
	
}
