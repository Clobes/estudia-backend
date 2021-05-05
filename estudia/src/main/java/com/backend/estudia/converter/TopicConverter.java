package com.backend.estudia.converter;

import com.backend.estudia.dto.TopicCreateDTO;
import com.backend.estudia.entity.Topic;

import lombok.AllArgsConstructor;

import com.backend.estudia.dto.TopicDTO;

@AllArgsConstructor
public class TopicConverter extends AbstractConverter<Topic, TopicDTO>{
	
	private LineConverter lineConverter;
	private HomeworkConverter homeworkConverter;
	
	@Override
	public TopicDTO fromEntity(Topic entity) {
		if(entity==null) return null;
		return TopicDTO.builder()
				.id(entity.getId())
				.name(entity.getName())
				.description(entity.getDescription())
				.lines(lineConverter.fromEntity(entity.getLines()))
				.homeworks(homeworkConverter.fromEntity((entity.getHomeworks())))
				.build();
	}

	@Override
	public Topic fromDTO(TopicDTO dto) {
		if(dto==null) return null;
		return Topic.builder()
				.id(dto.getId())
				.name(dto.getName())
				.description(dto.getDescription())
				.lines(lineConverter.fromDTO(dto.getLines()))
				.homeworks(homeworkConverter.fromDTO((dto.getHomeworks())))
				.build();
	}
	/*	
	private List<Line> fromLinesDTO(List<LineDTO> dto){
		if(dto == null ) return null;
		List<Line> lines = new ArrayList<Line>();
		for (LineDTO line : dto) {
			if(line instanceof ForumDTO) {
				Forum f = new Forum();
				f.setId(line.getId());
				f.setTitle(line.getTitle());
				f.setForumType(((ForumDTO) line).getForumType());
				f.setDescription(((ForumDTO) line).getDescription());
				lines.add(f);
			}
			if(line instanceof FileDTO) {
				File f = new File();
				f.setId(line.getId());
				f.setTitle(line.getTitle());
				f.setType(((FileDTO) line).getType());
				f.setData(((FileDTO) line).getData());
				lines.add(f);
			}
			if(line instanceof ContentPageDTO) {
				ContentPage f = new ContentPage();
				f.setId(line.getId());
				f.setTitle(line.getTitle());
				f.setContent(((ContentPageDTO) line).getContent());
				lines.add(f);
			}
		}
		return lines;
	}
	
	private List<LineDTO> fromLinesEntity(List<Line> lines){
		if(lines == null ) return null;
		List<LineDTO> dtos = new ArrayList<>();
		for (Line line : lines) {
			if(line instanceof Forum) {
				ForumDTO f = new ForumDTO();
				f.setId(line.getId());
				f.setTitle(line.getTitle());
				f.setForumType(((Forum) line).getForumType());
				f.setDescription(((Forum) line).getDescription());
				dtos.add(f);
			}
			if(line instanceof File) {
				FileDTO f = new FileDTO();
				f.setId(line.getId());
				f.setTitle(line.getTitle());
				f.setType(((File) line).getType());
				f.setData(((File) line).getData());
				dtos.add(f);
			}
			if(line instanceof ContentPage) {
				ContentPageDTO f = new ContentPageDTO();
				f.setId(line.getId());
				f.setTitle(line.getTitle());
				f.setContent(((ContentPage) line).getContent());
				dtos.add(f);
			}
		}
		return dtos;
	}
	*/
	public Topic createTopic(TopicCreateDTO topicCreateDTO) {
		if(topicCreateDTO==null) return null;
		return Topic.builder()
				.id(topicCreateDTO.getId())
				.name(topicCreateDTO.getName())
				.description(topicCreateDTO.getDescription())
				.build();
	}
}
