package com.project.medical_clinic_system.authTest;

import com.project.medical_clinic_system.controller.AuthController;
import com.project.medical_clinic_system.dto.request.LoginRequest;
import com.project.medical_clinic_system.dto.request.RegisterRequest;
import com.project.medical_clinic_system.dto.response.LoginResponse;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.model.User;
import com.project.medical_clinic_system.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void logout_logsOutThePrincipalByEmail_andReturns204() {
        User user = new Admin("Admin One", "admin@mail.com", "pw", "01012345678");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, List.of());

        ResponseEntity<Void> response = controller.logout(authentication);

        verify(authService).logout("admin@mail.com");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
