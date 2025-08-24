package com.example.pmt_backend.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DuplicateExceptionTest {

    @Test
    void duplicateException_ShouldStoreMessage() {
        String msg = "User already exists";
        DuplicateException ex = new DuplicateException(msg);
        
        assertEquals(msg, ex.getMessage());
    }
}
