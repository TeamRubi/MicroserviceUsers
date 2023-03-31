package com.gfttraining.exception;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ExceptionResponse {
	private Date timestamp;
	private String message;
	private List<String> details;

	public ExceptionResponse(Date timestamp, String message, List<String> errors) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = errors;
	}

	public ExceptionResponse(Date timestamp, String message) {
		this.timestamp = timestamp;
		this.message = message;
	}





}
