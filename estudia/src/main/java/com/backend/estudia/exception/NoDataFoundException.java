package com.backend.estudia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoDataFoundException extends RuntimeException {

	public NoDataFoundException() {
		super();
	}

	public NoDataFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoDataFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoDataFoundException(String message) {
		super(message);
	}

	public NoDataFoundException(Throwable cause) {
		super(cause);
	}
	
}
