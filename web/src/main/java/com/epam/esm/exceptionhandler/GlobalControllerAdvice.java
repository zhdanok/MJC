package com.epam.esm.exceptionhandler;

import com.epam.esm.exception.BadRequestException;
import com.epam.esm.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {

    public static final String CERTIFICATE_OR_TAG_NOT_FOUND = "40401";

    public static final String BAD_REQUEST = "40002";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorObject> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorObject errorObject = ErrorObject.builder()
                .errorMessage(ex.getLocalizedMessage())
                .errorCode(CERTIFICATE_OR_TAG_NOT_FOUND)
                .build();
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorObject> handleBadRequestException(BadRequestException ex) {
        ErrorObject errorObject = ErrorObject.builder()
                .errorMessage(ex.getLocalizedMessage())
                .errorCode(BAD_REQUEST)
                .build();
        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }
}
