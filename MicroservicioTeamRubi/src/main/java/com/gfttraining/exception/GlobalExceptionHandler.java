package com.gfttraining.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import com.gfttraining.UserMicroserviceApplication;
import ch.qos.logback.classic.Logger;



@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(UserMicroserviceApplication.class);

	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handlerException(EntityNotFoundException exception,WebRequest req){
		ExceptionResponse res = new ExceptionResponse(new Date(),exception.getMessage(),null);

		LOGGER.error(exception.getMessage());

		return new ResponseEntity<ExceptionResponse>(res, HttpStatus.NOT_FOUND);
	}; 

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {

		List<String> errors = new ArrayList<>();

		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
		}

		ExceptionResponse res = new ExceptionResponse(new Date(),"constraint violation", errors);

		LOGGER.error("createUser() -> " + errors.toString());

		return new ResponseEntity<ExceptionResponse>(res, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

		List<String> errors = fieldErrors.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.toList());

		ExceptionResponse res = new ExceptionResponse(new Date(),"method argument not valid", errors);

		return new ResponseEntity<ExceptionResponse>(res, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ExceptionResponse> handleResponseStatusException(ResponseStatusException ex) {

		List<String> errors = new ArrayList<>(Collections.singletonList(ex.getMessage()));

		ExceptionResponse res = new ExceptionResponse(new Date(),ex.getReason(), errors);

		return new ResponseEntity<ExceptionResponse>(res, HttpStatus.NOT_FOUND);
	}


	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(DuplicateEmailException ex) {

		ExceptionResponse res = new ExceptionResponse(ex.getMessage(), new Date());

		return new ResponseEntity<ExceptionResponse>(res, HttpStatus.CONFLICT);
	}



}
