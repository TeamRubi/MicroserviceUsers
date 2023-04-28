package com.gfttraining.exception;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handlerException(EntityNotFoundException exception,WebRequest req){
		ExceptionResponse res = new ExceptionResponse(LocalDate.now(), exception.getMessage(),null);

		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {

		List<String> errors = new ArrayList<>();

		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
		}

		ExceptionResponse res = new ExceptionResponse(LocalDate.now(),"constraint violation", errors);

		log.error("createUser() -> " + errors.toString());

		return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {

		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

		List<String> errors = fieldErrors.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.toList());

		ExceptionResponse res = new ExceptionResponse(LocalDate.now(),"method argument not valid", errors);

		log.error("createUser() -> " + errors.toString());

		return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ExceptionResponse> handleResponseStatusException(ResponseStatusException ex) {

		List<String> errors = new ArrayList<>(Collections.singletonList(ex.getMessage()));

		ExceptionResponse res = new ExceptionResponse(LocalDate.now(),ex.getReason(), errors);

		log.error(ex.getReason());

		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}


	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(DuplicateEmailException ex) {

		ExceptionResponse res = new ExceptionResponse(LocalDate.now(), ex.getMessage());

		log.error("Trying to insert an existing email to a user");

		return new ResponseEntity<>(res, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(DuplicateFavoriteException.class)
	public ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(DuplicateFavoriteException ex) {

		ExceptionResponse res = new ExceptionResponse(LocalDate.now(), ex.getMessage());

		log.error("Trying to insert an existing favorite product to a user");

		return new ResponseEntity<>(res, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(EmptyResultDataAccessException ex) {

		ExceptionResponse res = new ExceptionResponse(LocalDate.now(), ex.getMessage());

		log.error("Trying to delete a favorite product to a user");

		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(RestClientException.class)
	public ResponseEntity<ExceptionResponse> cantConnectWithExternalMicroservice(RestClientException ex){
		
		ExceptionResponse res = new ExceptionResponse(LocalDate.now(), ex.getMessage());

		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
		
	}




}
