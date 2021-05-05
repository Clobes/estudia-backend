package com.backend.estudia.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "certificates")
public class Certificate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "firstName", nullable = false, length = 20)
	private String firstName;
	@Column(name = "lastName", nullable = false, length = 20)
	private String lastaName;
	@Column(name = "ci", nullable = false, length = 20)
	private String ci;
	@Column(name = "courseName", nullable = false, length = 50)
	private String courseName;
	@Column(name = "courseDescription", nullable = false, length = 200)
	private String courseDescription;
	@Column(name = "amountHours", nullable = false)
	private int amountHours;
	@Column(name = "approvalDate", nullable = false)
	private LocalDateTime approvalDate;
	@Column(name = "score", nullable = false)
	private Double score;
	
}
