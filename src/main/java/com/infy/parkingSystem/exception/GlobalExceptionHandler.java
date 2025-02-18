/**
 * 
 */
package com.infy.parkingSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CustomErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		CustomErrorResponse customErrorResponse = new CustomErrorResponse(10001, errors.toString());
		return ResponseEntity.badRequest().body(customErrorResponse);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<CustomErrorResponse> handleRuntimeException(RuntimeException ex) {
		CustomErrorResponse customErrorResponse = new CustomErrorResponse(10002, ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(customErrorResponse);
	}
}