package com.project.medical_clinic_system.exceptionTest;

import com.project.medical_clinic_system.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final String PATH = "/api/test";

    @Mock
    private HttpServletRequest request;

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @BeforeEach
    void setUp() {
        lenient().when(request.getRequestURI()).thenReturn(PATH);
    }

    private void assertResponse(ResponseEntity<ErrorResponse> response, HttpStatus expectedStatus, String expectedMessage) {
        assertEquals(expectedStatus, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedStatus.value(), body.getStatus());
        assertEquals(expectedMessage, body.getMessage());
        assertEquals(PATH, body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleNotFound_returns404() {
        assertResponse(handler.handleNotFound(new ResourceNotFoundException("Patient not found"), request),
                HttpStatus.NOT_FOUND, "Patient not found");
    }

    @Test
    void handleDuplicate_returns409() {
        assertResponse(handler.handleDuplicate(new DuplicateResourceException("Email already exists"), request),
                HttpStatus.CONFLICT, "Email already exists");
    }

    @Test
    void handleInvalidAdminPassword_returns403() {
        assertResponse(handler.handleInvalidAdminPassword(
                        new InvalidAdminRegistrationPasswordException("Invalid admin registration password"), request),
                HttpStatus.FORBIDDEN, "Invalid admin registration password");
    }

    @Test
    void handleInvalidData_returns400() {
        assertResponse(handler.handleInvalidData(new InvalidDataException("bad data"), request),
                HttpStatus.BAD_REQUEST, "bad data");
    }

    @Test
    void handleAppointmentConflict_returns409() {
        assertResponse(handler.handleAppointmentConflict(
                        new AppointmentConflictException("Doctor already has an appointment at this time"), request),
                HttpStatus.CONFLICT, "Doctor already has an appointment at this time");
    }

    @Test
    void handleInvalidStatusTransition_returns409() {
        assertResponse(handler.handleInvalidStatusTransition(
                        new InvalidStatusTransitionException("Appointment cannot be confirmed"), request),
                HttpStatus.CONFLICT, "Appointment cannot be confirmed");
    }

    @Test
    void handleDoctorUnavailable_returns503() {
        ResponseEntity<ErrorResponse> response = handler.handleDoctorUnavailable(
                new DoctorUnavailableException("Doctor is not available at this time"), request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Doctor is not available at this time", response.getBody().getMessage());
        assertEquals(PATH, response.getBody().getPath());
    }

    /**
     * NOTE: this test currently FAILS and points to a real inconsistency in GlobalExceptionHandler:
     * handleDoctorUnavailable() sends HTTP 503 but writes 409 in the JSON body.
     * Fix: use the same HttpStatus for the body and for ResponseEntity.status(...).
     */
    @Test
    void handleDoctorUnavailable_bodyStatusMatchesHttpStatus() {
        ResponseEntity<ErrorResponse> response = handler.handleDoctorUnavailable(
                new DoctorUnavailableException("Doctor is not available at this time"), request);

        assertEquals(response.getStatusCode().value(), response.getBody().getStatus());
    }

    @Test
    void handleGeneral_returns500WithGenericMessage_andHidesRealError() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGeneral(new RuntimeException("db password leaked here"), request);

        assertResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        assertFalse(response.getBody().getMessage().contains("db password"));
    }
}
