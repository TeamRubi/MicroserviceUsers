package com.gfttraining.exception;

import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.gfttraining.UserMicroserviceApplication;

import ch.qos.logback.classic.Logger;
import lombok.Data;


@Data
public class ExceptionResponse {
	
	private Date timestamp;
	private String message;
	private List<String> details;

	public ExceptionResponse(Date timestamp, String message, List<String> details) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;

	}

	public ExceptionResponse(String message, Date timestamp) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		
	}

}
