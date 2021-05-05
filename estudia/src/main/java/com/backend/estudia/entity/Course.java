package com.backend.estudia.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

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
@Table(name="courses")
public class Course implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="name", nullable = false, length = 50, unique = true)
	private String name;
	@Column(name="creationDate", nullable = false)
	private LocalDateTime creationDate;
	@Column(name="area", nullable = false, length = 50)
	private String area;
	@Column(name="description", nullable = false, length = 200)
	private String description;
	@Column(name="amountHours", nullable = true)
	private int amountHours;
	@Column(name="certification", nullable = false)
	private boolean certification;
	@Column(name="minimunApproval", nullable=true)
	private int minimunApproval;
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="course_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Topic> topics;
	
	/* Alumnos inscriptos en el curso */
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Inscription> inscriptions;
	
	/* Docentes del curso */
	@ManyToMany(mappedBy = "teachedCourses")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<User> teachers;
	
	/* Calificaciones */
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Calification> califications;
	
}
