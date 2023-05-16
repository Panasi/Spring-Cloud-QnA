package com.panasi.qna.security.exception;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		ApiError apiError = new ApiError("No Handler Found", ex.getMessage());
		return new ResponseEntity<>(apiError, status);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ApiError apiError = new ApiError("Malformed JSON Request", ex.getMessage());
		return new ResponseEntity<>(apiError, status);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> validationList = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage()).toList();
		ApiError apiError = new ApiError("Validation Error", validationList.toString());
		return new ResponseEntity<>(apiError, status);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		ApiError apiError = new ApiError("Internal Exception", ex.getMessage());
		return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(NotFoundException.class)
	protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
		ApiError apiError = new ApiError("User not found", ex.getMessage());
		return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	protected ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
		ApiError apiError = new ApiError("Bad credentials", ex.getMessage());
		return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(DuplicateException.class)
	protected ResponseEntity<Object> handleDuplicateException(DuplicateException ex, WebRequest request) {
		ApiError apiError = new ApiError("Duplicate", ex.getMessage());
		return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
	}

}
