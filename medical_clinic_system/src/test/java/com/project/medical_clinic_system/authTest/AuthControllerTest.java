package com.project.medical_clinic_system.authTest;

import com.project.medical_clinic_system.controller.AuthController;
import com.project.medical_clinic_system.dto.request.LoginRequest;
import com.project.medical_clinic_system.dto.request.RegisterRequest;
import com.project.medical_clinic_system.dto.response.LoginResponse;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(authService);
    }

    @Test
    void register_returnsWhateverServiceReturns() {
        // RegisterRequest is a record (final) -> build a real one instead of mocking it
        RegisterRequest request = new RegisterRequest("Ahmed Mohamed", "a@mail.com", "Passw0rd@1", "01012345678",
                null, null, null, null, null, null, null, null, null);
        Object expected = new Object();
        when(authService.registerUser(request)).thenReturn(expected);

        assertSame(expected, controller.register(request));
    }

    @Test
    void login_delegatesToService() {
        LoginRequest request = mock(LoginRequest.class);
        LoginResponse expected = new LoginResponse(UUID.randomUUID(), "Admin One", Role.ADMIN, "jwt-token");
        when(authService.loginUser(request)).thenReturn(expected);

        LoginResponse result = controller.login(request);

        assertSame(expected, result);
        assertEquals("jwt-token", result.getToken());
    }
}
