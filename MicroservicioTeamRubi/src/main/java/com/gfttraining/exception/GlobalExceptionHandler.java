package com.gfttraining.exception;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;


@RestControllerAdvice
public class GlobalExceptionHandler  {


	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handlerException_entityNotFound(EntityNotFoundException exception,WebRequest req){
		ExceptionResponse res = new ExceptionResponse("Id no encontrado",new Date());

		return new ResponseEntity<ExceptionResponse>(res, HttpStatus.NOT_FOUND);
	};

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {

		List<String> errors = new ArrayList<>();

		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
		}

		ExceptionResponse res = new ExceptionResponse(new Date(),"constraint violation", errors);
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








}
