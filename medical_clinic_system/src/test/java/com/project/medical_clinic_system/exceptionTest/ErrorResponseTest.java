package com.project.medical_clinic_system.exceptionTest;

import com.project.medical_clinic_system.exception.ErrorResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {

    @Test
    void constructor_setsAllFields() {
        LocalDateTime now = LocalDateTime.now();

        ErrorResponse e = new ErrorResponse(now, 404, "Not found", "/api/x");

        assertEquals(now, e.getTimestamp());
        assertEquals(404, e.getStatus());
        assertEquals("Not found", e.getMessage());
        assertEquals("/api/x", e.getPath());
    }
}
