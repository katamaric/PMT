package com.example.pmt_backend.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @SuppressWarnings("deprecation")
	@Test
    void handleDuplicateEmailException_ShouldReturnBadRequest() {
        DuplicateException ex = new DuplicateException("Duplicate user");
        ResponseEntity<String> response = handler.handleDuplicateEmailException(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Duplicate user", response.getBody());
    }

    @SuppressWarnings("deprecation")
	@Test
    void handleInvalidEnumValue_ShouldReturnCustomRoleMessage() {
        // Mock the root cause message
        Throwable rootCause = new Throwable("Error converting value for enum com.example.pmt_backend.models.Role");
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMostSpecificCause()).thenReturn(rootCause);

        ResponseEntity<String> response = handler.handleInvalidEnumValue(ex);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Invalid role"));
    }

    @SuppressWarnings("deprecation")
	@Test
    void handleInvalidEnumValue_ShouldReturnGenericMessage() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMostSpecificCause()).thenReturn(new Throwable("Some other error"));

        ResponseEntity<String> response = handler.handleInvalidEnumValue(ex);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Invalid request format"));
    }
}
