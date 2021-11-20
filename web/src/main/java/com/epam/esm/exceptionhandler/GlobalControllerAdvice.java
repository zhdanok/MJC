package com.epam.esm.exceptionhandler;

import com.epam.esm.exception.AccessDeniedCustomException;
import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorObject> handleResourceNotFoundException(ResourceNotFoundException ex) {
		ErrorObject errorObject = ErrorObject.builder().errorMessage(ex.getLocalizedMessage())
				.errorCode(String.format("%d-%s", HttpStatus.NOT_FOUND.value(), ex.getErrCode())).build();
		return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorObject> handleBadRequestException(BadRequestException ex) {
		ErrorObject errorObject = ErrorObject.builder().errorMessage(ex.getLocalizedMessage())
				.errorCode(String.format("%d-%s", HttpStatus.BAD_REQUEST.value(), ex.getErrCode())).build();
		return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AccessDeniedCustomException.class)
	public ResponseEntity<ErrorObject> handleAccessDeniedCustomException(AccessDeniedCustomException ex) {
		ErrorObject errorObject = ErrorObject.builder().errorMessage(ex.getLocalizedMessage())
				.errorCode(String.format("%d-%s", HttpStatus.FORBIDDEN.value(), ex.getErrCode())).build();
		return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorObject> handleAccessDeniedException(AccessDeniedException ex) {
		ErrorObject errorObject = ErrorObject.builder().errorMessage(ex.getLocalizedMessage())
				.errorCode(String.format("%d", HttpStatus.FORBIDDEN.value())).build();
		return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
	}


}
