package com.backend.estudia.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.estudia.converter.LineConverter;
import com.backend.estudia.converter.TopicDiscussionConverter;
import com.backend.estudia.dto.LineCustomDTO;
import com.backend.estudia.dto.TopicDiscussionCustomDTO;
import com.backend.estudia.entity.Comment;
import com.backend.estudia.entity.ContentPage;
import com.backend.estudia.entity.Course;
import com.backend.estudia.entity.File;
import com.backend.estudia.entity.Forum;
import com.backend.estudia.entity.Inscription;
import com.backend.estudia.entity.Topic;
import com.backend.estudia.entity.TopicDiscussion;
import com.backend.estudia.entity.User;
import com.backend.estudia.exception.GeneralServiceException;
import com.backend.estudia.exception.NoDataFoundException;
import com.backend.estudia.exception.ValidateServiceException;
import com.backend.estudia.repository.ICommentRepository;
import com.backend.estudia.repository.ICourseRepository;
import com.backend.estudia.repository.ILineRepository;
import com.backend.estudia.repository.ITopicDiscussionRepository;
import com.backend.estudia.repository.ITopicRepository;
import com.backend.estudia.repository.IUserRepository;
import com.backend.estudia.util.LineType;
import com.backend.estudia.util.Reaction;
import com.backend.estudia.util.SendMail;
import com.backend.estudia.validator.ContentValidator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContentService {

	@Autowired
	private ICourseRepository courseRepo;

	@Autowired
	private ITopicRepository topicRepo;

	@Autowired
	private ILineRepository lineRepo;

	@Autowired
	private IUserRepository userRepo;

	@Autowired
	private ICommentRepository commentRepo;

	@Autowired
	private ITopicDiscussionRepository topicDiscussionRepo;

	@Autowired
	private CourseService courseService;
	
	@Autowired
	private LineConverter lineConverter;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private TopicDiscussionConverter topicDiscConverter;

	/* ##### Topics ###### */

	/* Crear o editar */
	@Transactional
	public Topic saveTopic(Topic topic, Long idCourse, Long idUser) {
		try {
			ContentValidator.topicCreate(topic);
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
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
			if(topic.getId() != null) {
				Topic updatedTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == topic.getId()).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
				updatedTopic.setName(topic.getName());
				updatedTopic.setDescription(topic.getDescription());
				return topicRepo.save(updatedTopic);
			}else {
				existsCourse.getTopics().add(topic);
				return topicRepo.save(topic);
			}
		} catch(ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch(Exception e) {
			log.info(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* Eliminar */
	@Transactional
	public void deleteTopic(Long idCourse, Long idUser, Long idTopic) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
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
			existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));	
			topicRepo.customDeleteTopicById(idTopic);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* ##### Content Page  ##### */

	/* Listar por ID */
	@Transactional
	public ContentPage getContentPageById(Long idCourse, Long idTopic, Long idUser, Long idPage) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			ContentPage contentPage = (ContentPage) existsTopic.getLines().stream().filter(cp -> cp.getId() == idPage).findFirst().orElseThrow(() -> new ValidateServiceException("Content page not found."));
			return contentPage;
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	//Update content page
	@Transactional
	public ContentPage contentPageUpdate(ContentPage contentPage, Long idCourse, Long idTopic, Long idUser) {
		ContentValidator.pageContentUpdate(contentPage, idCourse, idTopic, idUser);
		return contentPageCreateOrUpdate(contentPage, idCourse, idTopic, idUser) ;
	}
	//Create content page
	@Transactional
	public ContentPage contentPageCreate(ContentPage contentPage, Long idCourse, Long idTopic, Long idUser) {
		ContentValidator.pageContentCreate(contentPage, idCourse, idTopic, idUser);
		return contentPageCreateOrUpdate(contentPage, idCourse, idTopic, idUser) ;
	}

	private ContentPage contentPageCreateOrUpdate(ContentPage contentPage, Long idCourse, Long idTopic, Long idUser) {
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
			if(contentPage.getId() == null) {
				contentPage.setLineType(LineType.CONTENTPAGE);
				existsTopic.getLines().add(contentPage);
				return lineRepo.save(contentPage);
			}else {
				ContentPage updatedContentPage = (ContentPage) existsTopic.getLines().stream().filter(p -> p.getId() == contentPage.getId()).findFirst().orElseThrow(() -> new ValidateServiceException("Content page not found."));
				updatedContentPage.setTitle(contentPage.getTitle());
				updatedContentPage.setContent(contentPage.getContent());
				return lineRepo.save(updatedContentPage);
			}
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	@Transactional
	public void deleteContentPage(Long idCourse, Long idTopic, Long idUser, Long idPage) {
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
			existsTopic.getLines().stream().filter(p -> p.getId() == idPage).findFirst().orElseThrow(() -> new ValidateServiceException("Content page not found"));
			lineRepo.customDeleteLineById(idPage);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* ***** Archivos ***** */

	/* Descargar */
	@Transactional
	public File downloadFile(Long idCourse, Long idTopic, Long idUser, Long id) {
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
			return (File) existsTopic.getLines().stream().filter(f -> f.getId() == id).findFirst().orElseThrow(() -> new ValidateServiceException("File not found."));
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* Crear o Editar */
	@Transactional
	public File saveFile(Long idCourse, Long idTopic, Long idUser, File file) {
		try {
			ContentValidator.fileCreate(file);
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
			if(file.getId() == null) {
				file.setLineType(LineType.FILE);
				existsTopic.getLines().add(file);
				return lineRepo.save(file);
			}else {
				File updatedFile = (File) existsTopic.getLines().stream().filter(f -> f.getId() == file.getId()).findFirst().orElseThrow(() -> new ValidateServiceException("File not found."));
				updatedFile.setTitle(file.getTitle());
				updatedFile.setExtension(file.getExtension());
				updatedFile.setType(file.getType());
				updatedFile.setData(file.getData());
				return lineRepo.save(updatedFile);
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
	public void deleteFile(Long idCourse, Long idTopic, Long idUser, Long idFile) {
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
			existsTopic.getLines().stream().filter(f -> f.getId() == idFile).findFirst().orElseThrow(() -> new ValidateServiceException("File not found."));
			lineRepo.customDeleteLineById(idFile);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* ***** Foros ***** */

	/* Listar */
	@Transactional
	public LineCustomDTO listForum(Long idCourse, Long idTopic, Long idUser,  Long idForum) {
		try {
			Course existsCourse = courseRepo.findDistinctById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				// Verificamos que el usuario esté inscripto al curso
				if(!courseService.existsTeacherOnCourse(existsCourse, existsUser) && !courseService.existsStudentOnCourse(existsCourse, existsUser))
					throw new ValidateServiceException("The user is not a register on this course.");
			}
			 Forum forum = (Forum) existsTopic.getLines().stream().filter(f -> f.getId() == idForum).findFirst().orElseThrow(() -> new ValidateServiceException("Forum not found."));
			 return lineConverter.fromCustomLineEntity(forum, existsCourse.getId(), existsCourse.getName());
		} catch (ValidateServiceException | NoDataFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* Crear o editar */
	@Transactional 
	public Forum saveForum(Forum forum, Long idCourse, Long idTopic, Long idUser) {
		try {
			ContentValidator.forumCreate(forum);
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
			if(forum.getId() == null) {
				existsTopic.getLines().add(forum);
				return lineRepo.save(forum);
			}else {
				Forum updatedForum = (Forum) existsTopic.getLines().stream().filter(f -> f.getId() == forum.getId()).findFirst().orElseThrow(() -> new ValidateServiceException("Forum not found."));
				updatedForum.setTitle(forum.getTitle());
				updatedForum.setForumType(forum.getForumType());
				return lineRepo.save(updatedForum);
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
	public void deleteForum(Long idCourse, Long idTopic, Long idUser, Long idForum) {
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
					throw new ValidateServiceException("A student cannot delete a forum.");
				}
			}
			existsTopic.getLines().stream().filter(f -> f.getId() == idForum).findFirst().orElseThrow(() -> new ValidateServiceException("Forum not found."));
			lineRepo.customDeleteLineById(idForum);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* ##### Temas de discusión ##### */

	/* Listar */
	@Transactional
	public TopicDiscussionCustomDTO listTopicDiscussion(Long idCourse, Long idTopic, Long idForum, Long idUser, Long id) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Forum existsForum = (Forum) existsTopic.getLines().stream().filter(f -> f.getId() == idForum).findFirst().orElseThrow(() -> new ValidateServiceException("Forum not found."));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				/* Verificamos que el usuario esté inscripto al curso */
				if(!courseService.existsTeacherOnCourse(existsCourse, existsUser) && !courseService.existsStudentOnCourse(existsCourse, existsUser))
					throw new ValidateServiceException("The user is not a register on this course.");
			}
			TopicDiscussion td = existsForum.getTopicDiscussions().stream().filter(t -> t.getId() == id).findFirst().orElseThrow(() -> new ValidateServiceException("Topic discussion not found."));
			List<Comment> comments = commentRepo.findByTopicDiscIdOrderByCreationDate(td.getId());
			td.setOrderedComments(comments);
			return topicDiscConverter.fromEnityToTopicDiscussionCustom(td, existsForum.getId(), existsForum.getTitle(),
					existsCourse.getId(), existsCourse.getName());
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* Crear o editar */
	@Transactional
	public TopicDiscussion saveTopicDiscussion(TopicDiscussion topicDiscussion, Long idCourse, Long idTopic, Long idForum, Long idUser) {
		try {
			ContentValidator.topicDiscussionCreate(topicDiscussion);
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Forum existsForum = (Forum) existsTopic.getLines().stream().filter(f -> f.getId() == idForum).findFirst().orElseThrow(() -> new ValidateServiceException("Forum not found."));
			/* Nuevo tema de discusión */
			if(topicDiscussion.getId() == null) {
				/* Verificamos que no sea ADMIN */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
					/* Verificamos que el usuario esté inscripto al curso */
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser) && !courseService.existsStudentOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a register on this course.");
					if(courseService.existsStudentOnCourse(existsCourse, existsUser) && existsForum.getForumType().name().equalsIgnoreCase("NEWS")) {
						throw new ValidateServiceException("User with student role cannot add discussion topic in forum news.");
					}
				}
				topicDiscussion.setCreationDate(LocalDateTime.now());
				topicDiscussion.setUser(existsUser);
				existsForum.getTopicDiscussions().add(topicDiscussion);
				// Se debe notificar a todos los estudiantes del curso que tengan las notificaciones encendida
				List<Inscription> inscriptions = existsCourse.getInscriptions();
				User userAux = null;
				for (Inscription i: inscriptions) {
					userAux = i.getUser();
					if(existsForum.getForumType().name().equalsIgnoreCase("NEWS")) {
						if(userAux.isNotifications()) {
							notificationService.sendNotification("Nueva Novedad", "Se ha publicado una nueva novedad en el curso ".concat(existsCourse.getName()), userAux.getTokenNotification());
						}
						/* Se envía aviso vía mail a todos los estudiantes del curso */
						String msg = "Estimado " + userAux.getFirstName() + ", se ha publicado una nueva novedad en el curso " + existsCourse.getName() + ".<br>"; 
						msg += topicDiscussion.getTitle() + " - " + topicDiscussion.getContent() + " <br>";
						msg += "<b>Equipo e-Studia</b>";
						SendMail.Send(userAux.getEmail(), "Nueva Novedad - " + existsCourse.getName(), msg);
					}
				}
				return topicDiscussionRepo.save(topicDiscussion);
			/* Editar tema de discusión */
			}else {
				TopicDiscussion td = existsForum.getTopicDiscussions().stream().filter(t -> t.getId() == topicDiscussion.getId()).findFirst().orElseThrow(() -> new ValidateServiceException("Topic discussion not found."));
				/* Verificamos que no sea ADMIN */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
					/* Usuario docente del curso */
					if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findFirst().orElse(null) != null) {
						if(!courseService.existsTeacherOnCourse(existsCourse, existsUser))
							throw new ValidateServiceException("The user is not a teacher of this course.");
						/* El propio usuario del tema de discusión */
					}else {
						if(td.getUser().getId() != idUser)
							throw new ValidateServiceException("The user does not own this topic discussion.");
					}
				}
				td.setTitle(topicDiscussion.getTitle());
				td.setContent(topicDiscussion.getContent());
				return topicDiscussionRepo.save(td);
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
	public void deleteTopicDiscussion(Long idCourse, Long idTopic, Long idForum, Long idUser, Long id) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Forum existsForum = (Forum) existsTopic.getLines().stream().filter(f -> f.getId() == idForum).findFirst().orElseThrow(() -> new ValidateServiceException("Forum not found."));
			TopicDiscussion existsTD = existsForum.getTopicDiscussions().stream().filter(t -> t.getId() == id).findFirst().orElseThrow(() -> new ValidateServiceException("Topic discussion not found."));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				/* Usuario docente del curso */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("TEACHER")).findFirst().orElse(null) != null) {
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a teacher of this course.");
					/* El propio usuario del tema de discusión */
				}else {
					if(existsTD.getUser().getId() != idUser)
						throw new ValidateServiceException("The user does not own this topic discussion.");
				}
			}
			topicDiscussionRepo.customDeleteTopicDiscussionById(id);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

	/* ##### Comentarios en temas de discusión ##### */

	/* Crear o editar */
	@Transactional
	public Comment saveCommentTopicDiscussion(Comment comment, Long idCourse, Long idTopic, Long idForum, Long idTopicDiscussion,  Long idUser) {
		try {
			ContentValidator.commentTopicDiscussionCreate(comment);
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Forum existsForum = (Forum) existsTopic.getLines().stream().filter(f -> f.getId() == idForum).findFirst().orElseThrow(() -> new ValidateServiceException("Forum not found."));
			TopicDiscussion existsTD = existsForum.getTopicDiscussions().stream().filter(t -> t.getId() == idTopicDiscussion).findFirst().orElseThrow(() -> new ValidateServiceException("Topic discussion not found."));
			/* Nuevo comentario */
			if(comment.getId() == null) {
				/* Verificamos que no sea ADMIN */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
					/* Verificamos que el usuario esté inscripto al curso */
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser) && !courseService.existsStudentOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a register on this course.");
				}
				comment.setCreationDate(LocalDateTime.now());
				comment.setUser(existsUser);
				existsTD.getComments().add(comment);
				return commentRepo.save(comment);
			/* Editar un comentario*/
			} else {
				Comment existsComment = existsTD.getComments().stream().filter(c -> c.getId() == comment.getId()).findFirst().orElseThrow(() -> new ValidateServiceException("Comment not found."));
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
	public void deleteCommentTopicDiscussion(Long idCourse, Long idTopic, Long idForum, Long idTopicDiscussion, Long idUser, Long id) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Forum existsForum = (Forum) existsTopic.getLines().stream().filter(f -> f.getId() == idForum).findFirst().orElseThrow(() -> new ValidateServiceException("Forum not found."));
			TopicDiscussion existsTD = existsForum.getTopicDiscussions().stream().filter(t -> t.getId() == idTopicDiscussion).findFirst().orElseThrow(() -> new ValidateServiceException("Topic discussion not found."));
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
			commentRepo.customDeleteCommentById(id);
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
	public Comment saveResponseCommentTopicDiscussion(Comment response, Long idCourse, Long idTopic, Long idForum, Long idTopicDiscussion, Long idUser, Long idComment) {
		try {
			ContentValidator.commentTopicDiscussionCreate(response);
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Forum existsForum = (Forum) existsTopic.getLines().stream().filter(f -> f.getId() == idForum).findFirst().orElseThrow(() -> new ValidateServiceException("Forum not found."));
			TopicDiscussion existsTD = existsForum.getTopicDiscussions().stream().filter(t -> t.getId() == idTopicDiscussion).findFirst().orElseThrow(() -> new ValidateServiceException("Topic discussion not found."));
			Comment existsComment = existsTD.getComments().stream().filter(c -> c.getId() == idComment).findFirst().orElseThrow(() -> new ValidateServiceException("Comment not found."));
			/* Nuevo comentario */
			if(response.getId() == null) {
				/* Verificamos que no sea ADMIN */
				if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
					/* Verificamos que el usuario esté inscripto al curso */
					if(!courseService.existsTeacherOnCourse(existsCourse, existsUser) && !courseService.existsStudentOnCourse(existsCourse, existsUser))
						throw new ValidateServiceException("The user is not a register on this course.");
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

	/* ##### Reacciones ###### */

	/* Reaccionar */
	@Transactional
	public Comment reactionComment(Long idCourse, Long idTopic, Long idForum, Long idTopicDiscussion, Long idUser, Long id, Reaction reaction) {
		try {
			Course existsCourse = courseRepo.findById(idCourse).orElseThrow(() -> new ValidateServiceException("Course not found."));
			User existsUser = userRepo.findById(idUser).orElseThrow(() -> new ValidateServiceException("User not found."));
			Topic existsTopic = existsCourse.getTopics().stream().filter(t -> t.getId() == idTopic).findFirst().orElseThrow(() -> new ValidateServiceException("Topic not found."));
			Forum existsForum = (Forum) existsTopic.getLines().stream().filter(f -> f.getId() == idForum).findFirst().orElseThrow(() -> new ValidateServiceException("Forum not found."));
			TopicDiscussion existsTD = existsForum.getTopicDiscussions().stream().filter(t -> t.getId() == idTopicDiscussion).findFirst().orElseThrow(() -> new ValidateServiceException("Topic discussion not found."));
			/* Verificamos que no sea ADMIN */
			if(existsUser.getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findFirst().orElse(null) == null) {
				/* Verificamos que el usuario esté inscripto al curso */
				if(!courseService.existsTeacherOnCourse(existsCourse, existsUser) && !courseService.existsStudentOnCourse(existsCourse, existsUser))
					throw new ValidateServiceException("The user is not a register on this course.");
			}
			Comment comment = commentRepo.findById(id).orElseThrow(() -> new ValidateServiceException("Comment not found."));
			List<User> dislikes = comment.getDislikes();
			List<User> likes = comment.getLikes();
			/* Si es me gusta */
			if(reaction == Reaction.LIKE) {
				/* Verificamos si el usuario ya le dio dislike */
				if(dislikes != null) {
					User user = dislikes.stream().filter(u -> u.getId() == idUser).findFirst().orElse(null);
					if (user != null) {
						dislikes.remove(user);
					}
				}
				/* Verificamos si el usuario ya le había dado like */
				if(likes != null) {
					User user = likes.stream().filter(u -> u.getId() == idUser).findFirst().orElse(null);
					if(user == null) {
						likes.add(existsUser);
					}
				}
			}else {
				/* Verificamos si el usuario ya le dio like*/
				if(likes != null) {
					User user = likes.stream().filter(u -> u.getId() == idUser).findFirst().orElse(null);
					if (user != null) {
						likes.remove(user);
					}
				}
				/* Verificamos si el usuario ya le había dado dislike */
				if(dislikes != null) {
					User user = dislikes.stream().filter(u -> u.getId() == idUser).findFirst().orElse(null);
					if(user == null) {
						dislikes.add(existsUser);
					}
				}
			}
			comment.setDislikes(dislikes);
			comment.setLikes(likes);
			return commentRepo.save(comment);
		} catch (ValidateServiceException | NoDataFoundException e) {
			log.info(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}

}
