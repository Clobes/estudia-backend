package com.backend.estudia.entity;

import java.io.Serializable;
import java.time.LocalDate;
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
@Builder
@Entity
@Table(name="users")
public class User implements Serializable{

	private static final long serialVersionUID = 1L; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name="ci", nullable = false, unique = true, length = 20)
	private String ci;
	@Column(name="firstName", nullable = false, length = 20)
	private String firstName;
	@Column(name="lastName", nullable = false, length = 20)
	private String lastName;
	@Column(name="profileImage")
	@Lob
	private String profileImage;
	@Column(name="birthDate", length = 20)
	private LocalDate birthDate;
	@Column(name="email", nullable = false, unique = true, length = 100)
	private String email;
	@Column(name="password", nullable = false, length = 150)
	private String password;
	@Column(name="userDescription")
	@Lob
	private String userDescription;
	@Column(name="userStatus", nullable = true)
	@Builder.Default
	private boolean userStatus=true;

	// Notificaciones, solo para estudiantes
	@Column(name="notifications", nullable = false, length = 150)
	@Builder.Default
	private boolean notifications=true;
	@Column(name="tokenNotification", nullable = false, length = 250)
	private String tokenNotification;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Enumerated(EnumType.STRING)
	@JoinColumn(name="role_id")
	private List<Role> roles;
	
	/* Inscripciones a cursos, como alumno */
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Inscription> inscriptions;
	
	/* Cursos que dicta, como docente */
	@ManyToMany
	@JoinTable(name = "teached_courses", joinColumns = {@JoinColumn(name="user_id")}, inverseJoinColumns = {@JoinColumn(name="course_id")})
	private List<Course> teachedCourses;
	
	/* Calificaciones */
	@OneToMany(mappedBy = "user")
	private List<Calification> califications;
	
}
