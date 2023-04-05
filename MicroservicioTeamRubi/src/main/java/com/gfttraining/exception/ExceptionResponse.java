package com.gfttraining.exception;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.gfttraining.UserMicroserviceApplication;

import ch.qos.logback.classic.Logger;
import lombok.Data;


@Data
public class ExceptionResponse {

	private LocalDate localdate;
	private String message;
	private List<String> details;

	public ExceptionResponse(LocalDate localdate, String message, List<String> details) {
		super();
		this.localdate = localdate;
		this.message = message;
		this.details = details;

	}

	public ExceptionResponse(String message, LocalDate timestamp) {
		super();
		this.localdate = localdate;
		this.message = message;

	}

}
