package com.epam.esm.exceptionhandler;

import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorObject> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorObject errorObject = ErrorObject.builder()
                .errorMessage(ex.getLocalizedMessage())
                .errorCode(String.format("%d%s", HttpStatus.NOT_FOUND.value(), "01"))
                .build();
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorObject> handleBadRequestException(BadRequestException ex) {
        ErrorObject errorObject = ErrorObject.builder()
                .errorMessage(ex.getLocalizedMessage())
                .errorCode(String.format("%d%s", HttpStatus.BAD_REQUEST.value(), "02"))
                .build();
        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }
}