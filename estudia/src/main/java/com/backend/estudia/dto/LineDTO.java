package com.backend.estudia.dto;

import com.backend.estudia.util.LineType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class LineDTO {

	private Long id;
	private String title;
	private LineType lineType;
	
}
