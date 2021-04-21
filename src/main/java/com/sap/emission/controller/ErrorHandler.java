package com.sap.emission.controller;

import java.util.Optional;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sap.emission.Exceptions.HandledServiceException;
import com.sap.emission.Exceptions.HttpServerErrorExceptions;
import com.sap.emission.Exceptions.ResourceNotFoundException;
import com.sap.emission.data.CustomErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class ErrorHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.warn(ex.toString(), ex);
		CustomErrorResponse exceptionResponse = new CustomErrorResponse(extractReason(ex));
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class) // 404
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
			WebRequest request) {
		return exceptionToHttpError(ex, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(HttpServerErrorExceptions.class) // 404
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ResponseEntity<CustomErrorResponse> handleHttpServerErrorExceptions(ResourceNotFoundException ex,
			WebRequest request) {
		return exceptionToHttpError(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private static String extractReason(MethodArgumentNotValidException ex) {
		return Optional.ofNullable(ex).map(e -> e.getBindingResult()).map(result -> result.getFieldError())
				.map(error -> error.getDefaultMessage()).orElse("Arguments not valid");
	}


	private ResponseEntity<CustomErrorResponse> exceptionToHttpError(HandledServiceException ex,
			HttpStatus httpStatus) {
		log.warn(ex.toString(), ex);
		CustomErrorResponse exceptionResponse = new CustomErrorResponse(ex.getMessage());
		return new ResponseEntity<>(exceptionResponse, httpStatus);
	}
}
