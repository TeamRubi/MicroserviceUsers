package com.gfttraining.exception;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class ExceptionResponse {

	@NonNull
	private LocalDate localdate;
	@NonNull
	private String message;
	private List<String> details;


}
