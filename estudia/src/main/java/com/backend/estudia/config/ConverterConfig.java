package com.backend.estudia.config;

import java.time.format.DateTimeFormatter;

import com.backend.estudia.converter.*;
import com.backend.estudia.entity.HomeworkResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ConverterConfig {

	@Value("${config.datetimeFormat}")
	private String datetimeFormat;
	
	@Value("${config.dateFormat}")
	private String dateFormat;
	
	@Bean
	public CourseConverter getCourseConverter() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(datetimeFormat);
		return new CourseConverter(getTopicConverter(), format);
	}

	@Bean
	public TopicConverter getTopicConverter() {
		return new TopicConverter(getLineConverter(), getHomeworkConverter());
	}
	
	@Bean
	public LineConverter getLineConverter() {
		return new LineConverter(getTopicDiscussionConverter());
	}

	@Bean
	public HomeworkConverter getHomeworkConverter() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(datetimeFormat);
		return new HomeworkConverter(getHomeworkResultConverter(), format);
	}
	@Bean
	public HomeworkResultConverter getHomeworkResultConverter() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(datetimeFormat);
		return new HomeworkResultConverter(getCommentConverter(), format);
	}
	
	@Bean 
	public TopicDiscussionConverter getTopicDiscussionConverter() {
		return new TopicDiscussionConverter(getUserConverter(), getCommentConverter());
	}
	
	@Bean
	public CommentConverter getCommentConverter() {
		return new CommentConverter(getUserConverter());
	}
	
	@Bean
	public UserConverter getUserConverter() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(dateFormat);
		return new UserConverter(format);
	}
	
	@Bean
	public CalificationConverter getCalificationConverter() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(datetimeFormat);
		return new CalificationConverter(format);
	}
	
	@Bean
	public CertificateConverter getCertificateConverter() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(dateFormat);
		return new CertificateConverter(format);
	}
		
}
