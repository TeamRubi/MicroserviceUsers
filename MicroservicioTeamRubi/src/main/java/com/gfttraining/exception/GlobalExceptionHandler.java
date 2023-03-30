package com.gfttraining.exception;

import java.util.Date;

import javax.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handlerException(EntityNotFoundException exception,WebRequest req){
		ExceptionResponse res = new ExceptionResponse(new Date(),exception.getMessage(),null);
		
		return new ResponseEntity<ExceptionResponse>(res, HttpStatus.NOT_FOUND);
	};
}
