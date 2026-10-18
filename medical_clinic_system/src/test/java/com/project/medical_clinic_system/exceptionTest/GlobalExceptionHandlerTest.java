package com.project.medical_clinic_system.exceptionTest;

import com.project.medical_clinic_system.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

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
    void handleDoctorUnavailable_returns409_andBodyMatchesHttpStatus() {
        ResponseEntity<ErrorResponse> response = handler.handleDoctorUnavailable(
                new DoctorUnavailableException("Doctor is not available at this time"), request);

        assertResponse(response, HttpStatus.CONFLICT, "Doctor is not available at this time");
        assertEquals(response.getStatusCode().value(), response.getBody().getStatus());
    }

    @Test
    void handleAccessDenied_returns403() {
        assertResponse(handler.handleAccessDenied(new AccessDeniedException("nope"), request),
                HttpStatus.FORBIDDEN, "Access denied");
    }

    @Test
    void handleAccessDenied_doesNotLeakOriginalMessage() {
        ResponseEntity<ErrorResponse> response =
                handler.handleAccessDenied(new AccessDeniedException("internal rule xyz failed"), request);

        assertFalse(response.getBody().getMessage().contains("xyz"));
    }

    @Test
    void handleGeneral_returns500WithGenericMessage_andHidesRealError() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGeneral(new RuntimeException("db password leaked here"), request);

        assertResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        assertFalse(response.getBody().getMessage().contains("db password"));
    }

    // ---------- request validation / parsing errors ----------

    private MethodParameter anyMethodParameter() throws NoSuchMethodException {
        // parameter index -1 = the method's return type; any real method works for these tests
        return new MethodParameter(Object.class.getMethod("toString"), -1);
    }

    @Test
    void handleValidation_returns400AndListsEveryFieldError() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "name", "Name is required"));
        bindingResult.addError(new FieldError("request", "email", "Invalid email"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(anyMethodParameter(), bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, request);

        assertResponse(response, HttpStatus.BAD_REQUEST, "name: Name is required, email: Invalid email");
    }

    @Test
    void handleValidation_singleError_hasNoSeparator() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "password", "invalid password"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(anyMethodParameter(), bindingResult);

        assertResponse(handler.handleValidation(ex, request), HttpStatus.BAD_REQUEST, "password: invalid password");
    }

    @Test
    void handleUnreadable_returns400WithGenericMessage() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        assertResponse(handler.handleUnreadable(ex, request),
                HttpStatus.BAD_REQUEST, "Malformed or unreadable request body");
    }

    @Test
    void handleTypeMismatch_returns400WithParameterName() throws Exception {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "not-a-uuid", UUID.class, "patientID", anyMethodParameter(), new IllegalArgumentException("bad"));

        assertResponse(handler.handleTypeMismatch(ex, request),
                HttpStatus.BAD_REQUEST, "Invalid value for parameter 'patientID'");
    }

    @Test
    void handleMissingParam_returns400WithParameterName() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("sortBy", "String");

        assertResponse(handler.handleMissingParam(ex, request),
                HttpStatus.BAD_REQUEST, "Missing parameter 'sortBy'");
    }

    @Test
    void handleDataIntegrity_returns409_andHidesDatabaseDetails() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("duplicate key value violates unique constraint uk_email");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(ex, request);

        assertResponse(response, HttpStatus.CONFLICT, "Request conflicts with existing data");
        assertFalse(response.getBody().getMessage().contains("uk_email"));
    }
}
