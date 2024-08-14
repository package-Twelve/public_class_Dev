package com.sparta.publicclassdev.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ExceptionResponse> defaultException(Exception e){
		e.printStackTrace();
		ExceptionResponse exceptionResponse = ExceptionResponse.builder()
				.statusCode(ErrorCode.FAIL.getStatus())
				.message(ErrorCode.FAIL.getMessage())
				.build();
		return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ExceptionResponse> handleInvalidPasswordException(CustomException e) {
		ExceptionResponse exceptionResponse = ExceptionResponse.builder()
				.statusCode(e.getErrorCode().getStatus())
				.message(e.getErrorCode().getMessage())
				.build();
		return new ResponseEntity<>(exceptionResponse, HttpStatusCode.valueOf(e.getErrorCode().getStatus()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e) {
		ExceptionResponse exceptionResponse = ExceptionResponse.builder()
				.statusCode(ErrorCode.UNAUTHORIZED_MANAGER.getStatus())
				.message(ErrorCode.UNAUTHORIZED_MANAGER.getMessage())
				.build();
		return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> processValidationError(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		FieldError fieldError = bindingResult.getFieldErrors().get(0);
		String msg = fieldError.getDefaultMessage();
		ExceptionResponse exceptionResponse = ExceptionResponse.builder()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.message(msg)
				.build();
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}
}