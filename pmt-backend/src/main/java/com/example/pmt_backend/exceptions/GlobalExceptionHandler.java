package com.example.pmt_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleDuplicateEmailException(DuplicateException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidEnumValue(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getMostSpecificCause();
        
        if (rootCause != null && rootCause.getMessage().contains("com.example.pmt_backend.models.Role")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid role. Allowed values: MEMBER, OBSERVER, ADMIN");
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid request format. Please check your input.");
    }
}
