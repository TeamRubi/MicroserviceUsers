package com.gfttraining.exception;

import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.gfttraining.UserMicroserviceApplication;

import ch.qos.logback.classic.Logger;
import lombok.Data;


@Data
public class ExceptionResponse {
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(UserMicroserviceApplication.class);

	private Date timestamp;
	private String message;
	private List<String> details;

	public ExceptionResponse(Date timestamp, String message, List<String> errors) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
		
		LOGGER.error("createUser() -> " + details.toString());

	}

	public ExceptionResponse(String message, Date timestamp) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		
		LOGGER.error(message);
	}

}
