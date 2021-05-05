package com.backend.estudia.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
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
@Entity
@Builder
@Table(name="content_pages")
@OnDelete(action = OnDeleteAction.CASCADE)
public class ContentPage extends Line implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name="content")
	@Lob
	private String content;
}
