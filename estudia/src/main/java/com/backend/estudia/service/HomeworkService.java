package com.backend.estudia.service;

import com.backend.estudia.entity.*;
import com.backend.estudia.exception.GeneralServiceException;
import com.backend.estudia.exception.NoDataFoundException;
import com.backend.estudia.exception.ValidateServiceException;
import com.backend.estudia.repository.*;
import com.backend.estudia.util.LineType;
import com.backend.estudia.validator.ContentValidator;
import com.backend.estudia.util.SendMail;
import com.backend.estudia.validator.CourseValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.estudia.validator.HomeworkValidator;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HomeworkService {

	@Autowired
	private IHomeworkRepository homeworkRepo;

	@Autowired
	private IHomeworkResultRepository homeworkResultRepo;

	@Autowired
	private ITopicRepository topicRepo;

	@Autowired
	private ICourseRepository courseRepo;

	@Autowired
	private IUserRepository userRepo;

	@Autowired
	private CourseService courseService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ICommentRepository commentRepo;

	public Page<Homework> getAllHomeworks(Pageable pageable){
		try {
			return homeworkRepo.findAll(pageable);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* ##### Homeworks ##### */
	
	/* Listar */
	@Transactional
	public Homework listHomework(Long idCourse, Long idTopic, Long idHomework, Long idUser) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				// Verificamos que el usuario esté inscripto al curso
				if(!courseService.existsTeacherOnCourse(existsCourse, existsUser) && !courseService.existsStudentOnCourse(existsCourse, existsUser))
					throw new ValidateServiceException("The user is not a register on this course.");
			}
			return existsTopic.getHomeworks().stream().filter(h -> h.getId() == idHomework).findFirst().orElseThrow(() -> new ValidateServiceException("Homework not found"));
		} catch (ValidateServiceException | NoDataFoundException e) {
			//log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			//log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* Crear */
	@Transactional
	public Homework create(Homework homework, String courseName, String topicName, Long idUser) {
		try {
			HomeworkValidator.create(homework);
			// TODO: Verificar que el rol del creador sea Docente            
			User existsUser = userRepo.findById(idUser).orElse(null);
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findFirst().orElse(null) != null) {

				homework.setUser(existsUser);
				homeworkRepo.save(homework);

				// Guardo la tarea en el Tema correspondiente al Curso
				if (courseName != null && !courseName.trim().isEmpty()) {
					Course course = courseRepo.findByName(courseName).orElse(null);
					List<Topic> topics = course.getTopics();
					Topic topic = null;
					List<Homework> homeworks = null;
					// Seteo la tarea en el Tema que se indicó
					if ((topicName != null && !topicName.trim().isEmpty())) {
						for(Topic t : topics) {
							if(t.getName().equals(topicName)) {
								topic = t;
								break;
							}
						}
						if (topic != null) {
							// Agrego la tarea al Tema
							homeworks = topic.getHomeworks();
							homeworks.add(homework);
							topic.setHomeworks(homeworks);
							topicRepo.save(topic);
						} else {
							// Tema no válido
							throw new ValidateServiceException("The topic name is not valid.");
						}
					} else {
						// En caso contrario asigno el tema por defecto
						topic = topics.get(0);
						if (topic != null) {
							// Agrego la tarea al Tema
							homeworks = topic.getHomeworks();
							homeworks.add(homework);
							topic.setHomeworks(homeworks);
							topicRepo.save(topic);
						} else {
							// Tema no válido
							throw new ValidateServiceException("The course not have any topics.");
						}
					}

					course.setTopics(topics);
					courseRepo.save(course);

					// Se debe notificar a todos los estudiantes del curso que tengan las notificaciones encendida
					List<Inscription> inscriptions = course.getInscriptions();
					User userAux = null;
					for (Inscription i: inscriptions) {
						userAux = i.getUser();
						if(userAux.isNotifications()) {
							notificationService.sendNotification("Nueva Tarea", "Se ha publicado una nueva tarea en el curso ".concat(courseName), userAux.getTokenNotification());
						}
						/* Se envía aviso vía mail a todos los estudiantes del curso */
						String msg = "Estimado " + userAux.getFirstName() + ", se ha publicado una nueva tarea en el curso " + courseName + ".<br>"; 
						msg += homework.getTitle() + " - " + homework.getDescription() + " <br>";
						msg += "<b>Equipo e-Studia</b>";
						SendMail.Send(userAux.getEmail(), "Nueva Tarea - " + courseName, msg);
					}
					return homework;
				} else {
					throw new ValidateServiceException("Must have to indicate the Course.");
				}
			} else {
				throw new ValidateServiceException("A student cannot create a Homework.");
			}

		} catch(ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch(Exception e) {
			log.info(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* Editar */
	@Transactional
	public Homework edit(Homework homework, Long idCourse, Long idTopic, Long idUser) {
		try {
			HomeworkValidator.create(homework);
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				/* Usuario docente del curso */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findFirst().orElse(null) != null) {
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a teacher of this course.");
				} else {
					throw new ValidateServiceException("A student cannot modify the course structure.");
				}
			}
			Homework updateHomework = existsTopic.getHomeworks().stream().filter(h -> h.getId() == homework.getId()).findFirst().orElseThrow(() -> new ValidateServiceException("Homework not found"));
			updateHomework.setTitle(homework.getTitle());
			updateHomework.setDescription(homework.getDescription());
			updateHomework.setLimitDate(homework.getLimitDate());
			updateHomework.setDeliveryFormat(homework.getDeliveryFormat());
			updateHomework.setUser(existsUser);
			return homeworkRepo.save(updateHomework);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* Eliminar */
	@Transactional
	public void delete(Long idCourse, Long idTopic, Long idUser, Long id) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				/* Usuario docente del curso */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findFirst().orElse(null) != null) {
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a teacher of this course.");
				} else {
					throw new ValidateServiceException("A student cannot modify the course structure.");
				}
			}
			existsTopic.getHomeworks().stream().filter(h -> h.getId() == id).findFirst().orElseThrow(() -> new ValidateServiceException("Homework not found"));
			homeworkRepo.customDeleteHomeworkById(id);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* ##### Homeworks Result ##### */

	/* Crear */
	@Transactional
	public HomeworkResult createHomeworkResult(Long idCourse, Long idTopic, Long idHomework, Long idUser, HomeworkResult homeworkResult) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Homework existsHomework = existsTopic.getHomeworks().stream().filter(h -> h.getId() == idHomework).findFirst().orElseThrow(() -> new ValidateServiceException("Homework not found"));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				/* Verificamos que el usuario sea estudiante del curso */
				if(!courseService.existsStudentOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a student of this course.");
			}
			/* Verifico que el usuario no haya entregado ya */
			HomeworkResult existsHomeworkResult = existsHomework.getHomeworkResults().stream().filter(hr -> hr.getUser().getId() == existsUser.getId()).findFirst().orElse(null);
			if(existsHomeworkResult != null) throw new ValidateServiceException("The user already sent the homework.");
			homeworkResult.setUser(existsUser);
			existsHomework.getHomeworkResults().add(homeworkResult);
			return homeworkResultRepo.save(homeworkResult);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	/* Editar */
	@Transactional
	public HomeworkResult editHomeworkResult(Long idCourse, Long idTopic, Long idHomework, Long idUser, HomeworkResult homeworkResult) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Homework existsHomework = existsTopic.getHomeworks().stream().filter(h -> h.getId() == idHomework).findFirst().orElseThrow(() -> new ValidateServiceException("Homework not found"));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				/* Verificamos que el usuario sea estudiante del curso */
				if(!courseService.existsStudentOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a student of this course.");
			}
			HomeworkResult updatedFile = existsHomework.getHomeworkResults().stream().filter(h -> h.getUser().getId() == idUser).findFirst().orElseThrow(() -> new ValidateServiceException("Homework result not found."));
			updatedFile.setTitle(homeworkResult.getTitle());
			updatedFile.setContent(homeworkResult.getContent());
			updatedFile.setExtension(homeworkResult.getExtension());
			updatedFile.setData(homeworkResult.getData());
			return homeworkResultRepo.save(updatedFile);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* Descargar Entrega */
	@Transactional
	public HomeworkResult getHomeworkResult(Long idCourse, Long idTopic, Long idHomework, Long idUser, Long id) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Homework existsHomework = existsTopic.getHomeworks().stream().filter(h -> h.getId() == idHomework).findFirst().orElseThrow(() -> new ValidateServiceException("Homework not found"));
			HomeworkResult existsHomeworkResult = existsHomework.getHomeworkResults().stream().filter(h -> h.getId() == id).findFirst().orElseThrow(() -> new ValidateServiceException("Homework result not found."));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				/* Usuario docente del curso */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findFirst().orElse(null) != null) {
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a teacher of this course.");
				} else {
					/* Que sea el dueño de la tarea */
					if(existsHomeworkResult.getUser().getId() != existsUser.getId())
						throw new ValidateServiceException("The user is not the owner of this homework result.");
				}
			}
			return existsHomeworkResult;
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* ##### Comentarios en entregas ##### */

	/* Crear o editar */
	@Transactional
	public Comment saveCommentHomeworkResult(Comment comment, Long idCourse, Long idTopic, Long idHomework, Long idHomeworkResult, Long idUser) {
		try {
			ContentValidator.commentTopicDiscussionCreate(comment);
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Homework existsHomework = existsTopic.getHomeworks().stream().filter(h -> h.getId() == idHomework).findFirst().orElseThrow(() -> new ValidateServiceException("Homework not found"));
			HomeworkResult existsHomeworkResult = existsHomework.getHomeworkResults().stream().filter(hr -> hr.getId() == idHomeworkResult).findFirst().orElseThrow(() -> new ValidateServiceException("Homework result not found."));
			/* Nuevo comentario */
			if(comment.getId() == null) {
				/* Verificamos que no sea ADMIN */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
					/* Verificamos que el usuario este inscripto en el curso */
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser) && !courseService.existsStudentOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a register on this course.");
					/* Verificamos que si no es docente, tiene que ser el dueño de la tarea */
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser)) {
						if(existsUser.getId() != existsHomeworkResult.getId())
							throw new ValidateServiceException("The student is not the owner of this homework result.");
					}
				}
				comment.setCreationDate(LocalDateTime.now());
				comment.setUser(existsUser);
				existsHomeworkResult.getComments().add(comment);
				return commentRepo.save(comment);
				/* Editar un comentario*/
			} else {
				Comment existsComment = commentRepo.findById(comment.getId()).orElseThrow(() -> new ValidateServiceException("Comment not found."));
				/* Verificamos que no sea ADMIN */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
					/* Usuario es docente del curso */
					if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findFirst().orElse(null) != null) {
						if(!courseService.existsTeacherOnCourse(existsCourse, existsUser))
							throw new ValidateServiceException("The user is not a teacher of this course.");
					/* El propio usuario del mensaje */
					}else {
						if(existsComment.getUser().getId() != idUser)
							throw new ValidateServiceException("The user does not own this comment.");
					}
				}
				existsComment.setContent(comment.getContent());
				return commentRepo.save(existsComment);
			}
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* Eliminar */
	@Transactional
	public void deleteCommentHomeworkResult(Long idCourse, Long idTopic, Long idHomework, Long idHomeworkResult, Long idUser, Long id) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Homework existsHomework = existsTopic.getHomeworks().stream().filter(h -> h.getId() == idHomework).findFirst().orElseThrow(() -> new ValidateServiceException("Homework not found"));
			HomeworkResult existsHomeworkResult = existsHomework.getHomeworkResults().stream().filter(hr -> hr.getId() == idHomeworkResult).findFirst().orElseThrow(() -> new ValidateServiceException("Homework result not found."));
			Comment existsComment = commentRepo.findById(id).orElseThrow(() -> new ValidateServiceException("Comment not found."));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				/* Usuario docente del curso */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findFirst().orElse(null) != null) {
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a teacher of this course.");
				/* El propio usuario del mensaje */
				}else {
					if(existsComment.getUser().getId() != idUser)
						throw new ValidateServiceException("The user does not own this comment.");
				}
			}
			commentRepo.customDeleteCommentById(id);;
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* Responder a un comentario */
	@Transactional
	public Comment saveResponseCommentHomeworkResult(Comment response, Long idCourse, Long idTopic, Long idHomework, Long idHomeworkResult, Long idUser, Long idComment) {
		try {
			ContentValidator.commentTopicDiscussionCreate(response);
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Homework existsHomework = existsTopic.getHomeworks().stream().filter(h -> h.getId() == idHomework).findFirst().orElseThrow(() -> new ValidateServiceException("Homework not found"));
			HomeworkResult existsHomeworkResult = existsHomework.getHomeworkResults().stream().filter(hr -> hr.getId() == idHomeworkResult).findFirst().orElseThrow(() -> new ValidateServiceException("Homework result not found."));
			Comment existsComment = commentRepo.findById(idComment).orElseThrow(() -> new ValidateServiceException("Comment not found."));
			/* Nuevo comentario */
			if(response.getId() == null) {
				/* Verificamos que no sea ADMIN */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
					/* Verificamos que el usuario este inscripto en el curso */
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser) && !courseService.existsStudentOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a register on this course.");
					/* Verificamos que si no es docente, tiene que ser el dueño de la tarea */
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser)) {
						if(existsUser.getId() != existsHomeworkResult.getId())
							throw new ValidateServiceException("The student is not the owner of this homework result.");
					}
				}
				response.setCreationDate(LocalDateTime.now());
				response.setUser(existsUser);
				response.setParent_comment(existsComment);
				existsComment.getResponses().add(response);
				return commentRepo.save(response);
				/* Editar un comentario*/
			} else {
				Comment existsResponse = existsComment.getResponses().stream().filter(r -> r.getId() == response.getId()).findFirst().orElseThrow(() -> new ValidateServiceException("Comment not found."));
				/* Verificamos que no sea ADMIN */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
					/* Usuario es docente del curso */
					if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findFirst().orElse(null) != null) {
						if(!courseService.existsTeacherOnCourse(existsCourse, existsUser))
							throw new ValidateServiceException("The user is not a teacher of this course.");
					/* El propio usuario del mensaje */
					} else {
						if(existsResponse.getUser().getId() != idUser)
							throw new ValidateServiceException("The user does not own this response.");
					}
				}
				existsResponse.setContent(response.getContent());
				return commentRepo.save(existsResponse);
			}
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	public Boolean verifyHomework(Long idCourse, Long idTopic, Long idHomework, Long idUser) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Homework existsHomework = existsTopic.getHomeworks().stream().filter(h -> h.getId() == idHomework).findFirst().orElseThrow(() -> new ValidateServiceException("Homework not found"));
			HomeworkResult existsHomeworkResult = existsHomework.getHomeworkResults().stream().filter(hr -> hr.getUser().getId() == idUser).findFirst().orElse(null);
			if(existsHomeworkResult == null) {
				return false;
			}else {
				return true;
			}
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
}
