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
public class WrapperResponse <T> {

	private Boolean ok;
	private String message;
	private T body;
	
	public ResponseEntity<WrapperResponse<T>> createResponse(HttpStatus status){
		return new ResponseEntity<>(this, status);
	}
	
}
