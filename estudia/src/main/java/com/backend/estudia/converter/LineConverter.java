package com.backend.estudia.converter;


import org.springframework.web.multipart.MultipartFile;

import com.backend.estudia.dto.ContentPageCreateDTO;
import com.backend.estudia.dto.ContentPageDTO;
import com.backend.estudia.dto.ContentPageUpdateDTO;
import com.backend.estudia.dto.FileDTO;
import com.backend.estudia.dto.FileSaveDTO;
import com.backend.estudia.dto.ForumDTO;
import com.backend.estudia.dto.ForumSaveDTO;
import com.backend.estudia.dto.LineCustomDTO;
import com.backend.estudia.dto.LineDTO;
import com.backend.estudia.entity.ContentPage;
import com.backend.estudia.entity.Course;
import com.backend.estudia.entity.File;
import com.backend.estudia.entity.Forum;
import com.backend.estudia.entity.Line;
import com.backend.estudia.exception.GeneralServiceException;
import com.backend.estudia.util.ForumType;
import com.backend.estudia.util.LineType;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class LineConverter extends AbstractConverter<Line, LineDTO>{
	
	private TopicDiscussionConverter topicDiscsConverter;

	@Override
	public LineDTO fromEntity(Line entity) {
		if(entity==null) return null;
		LineDTO dto=null;
		if(entity instanceof Forum) {
			ForumDTO f = new ForumDTO();
			f.setId(entity.getId());
			f.setTitle(entity.getTitle());
			f.setLineType(entity.getLineType());
			f.setForumType(((Forum) entity).getForumType());
			f.setDescription(((Forum) entity).getDescription());
			f.setTopicDiscussions(topicDiscsConverter.fromEntity(((Forum) entity).getTopicDiscussions()));
			dto = f;
		}
		if(entity instanceof File) {
			FileDTO f = new FileDTO();
			f.setId(entity.getId());
			f.setTitle(entity.getTitle());
			f.setLineType(entity.getLineType());
			f.setType(((File) entity).getType());
			f.setData(((File) entity).getData());
			f.setExtension(((File) entity).getExtension());
			dto = f;
		}
		if(entity instanceof ContentPage) {
			ContentPageDTO f = new ContentPageDTO();
			f.setId(entity.getId());
			f.setTitle(entity.getTitle());
			f.setLineType(entity.getLineType());
			f.setContent(((ContentPage) entity).getContent());
			dto = f;
		}
		return dto;
	}
		
	@Override
	public Line fromDTO(LineDTO dto) {
		if(dto==null) return null;
		Line entity=null;
		if(dto instanceof ForumDTO) {
			Forum f = new Forum();
			f.setId(dto.getId());
			f.setTitle(dto.getTitle());
			f.setForumType(((ForumDTO) dto).getForumType());
			f.setDescription(((ForumDTO) dto).getDescription());
			entity = f;
		}
		if(dto instanceof FileDTO) {
			File f = new File();
			f.setId(dto.getId());
			f.setTitle(dto.getTitle());
			f.setType(((FileDTO) dto).getType());
			f.setData(((FileDTO) dto).getData());
			entity = f;
		}
		if(dto instanceof ContentPageDTO) {
			ContentPage f = new ContentPage();
			f.setId(dto.getId());
			f.setTitle(dto.getTitle());
			f.setContent(((ContentPageDTO) dto).getContent());
			entity = f;
		}
		return entity;
	}
	
	public ContentPage createContentPage(ContentPageCreateDTO contentPageCreateDTO) {
		if(contentPageCreateDTO==null) return null;
		ContentPage c = new ContentPage();
		c.setTitle(contentPageCreateDTO.getTitle());
		c.setContent(contentPageCreateDTO.getContent());
		return c;
				
	}
	
	public ContentPage updateContentPage(ContentPageUpdateDTO contentPageUpdateDTO) {
		if(contentPageUpdateDTO==null) return null;
		ContentPage c = new ContentPage();
		c.setId(contentPageUpdateDTO.getIdContent());
		c.setTitle(contentPageUpdateDTO.getTitle());
		c.setContent(contentPageUpdateDTO.getContent());
		return c;
				
	}

	public Forum saveForum(ForumSaveDTO dto) {
		if(dto == null) return null;
		Forum f = new Forum();
		if(dto.getId() != null) {
			f.setId(dto.getId());
		}
		f.setTitle(dto.getTitle());
		f.setDescription(dto.getDescription());
		switch (dto.getForumType()) {
			case "NEWS": {
				f.setForumType(ForumType.NEWS);
				break;
			}
			case "QUESTIONS": {
				f.setForumType(ForumType.QUESTIONS);
				break;
			}
			default:
				f.setForumType(ForumType.GENERAL);
				break;
		}
		f.setLineType(LineType.FORUM);
		return f;
	}
	
	public File saveFile(Long id, String title, MultipartFile file) {
		try {
			File f = new File();
			if(id != null) {
				f.setId(id);
			}
			f.setTitle(title);
			f.setType(file.getContentType());
			f.setLineType(LineType.FILE);
			f.setExtension(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
			f.setData(file.getBytes());
			return f;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new GeneralServiceException(e.getMessage(), e);
		}
	}
	
	public File createContentFile(ContentPageCreateDTO contentPageCreateDTO) {
		if(contentPageCreateDTO==null) return null;
		File f = new File();
		f.setTitle(contentPageCreateDTO.getTitle());
		return f;
				
	}
	
	public LineCustomDTO fromCustomLineEntity(Line entity, Long courseId, String courseName ) {
		if(entity==null) return null;
		return LineCustomDTO.builder()
				.lineDto(fromEntity(entity))
				.courseId(courseId)
				.courseName(courseName)
				.build();
	}
}
