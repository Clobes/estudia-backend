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
@Table(name="files")
@OnDelete(action = OnDeleteAction.CASCADE)
public class File extends Line implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="type", length = 50)
	private String type;
	@Column(name="extension", length = 10)
	private String extension;
	@Column(name="data")
	@Lob
	private byte[] data;

}
