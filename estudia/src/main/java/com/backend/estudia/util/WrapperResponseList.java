package com.backend.estudia.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WrapperResponseList<T> {

	private Boolean ok;
	private long totalElements;
	private int totalPages;
	private String message;
	private T data;
	
	public ResponseEntity<WrapperResponseList<T>> createResponse(HttpStatus status){
		return new ResponseEntity<>(this, status);
	}
}
