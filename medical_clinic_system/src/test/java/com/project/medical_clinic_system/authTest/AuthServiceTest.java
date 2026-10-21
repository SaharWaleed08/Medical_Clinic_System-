package com.project.medical_clinic_system.authTest;

import com.project.medical_clinic_system.dto.request.*;
import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.dto.response.LoginResponse;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.exception.ActiveSessionException;
import com.project.medical_clinic_system.exception.InvalidDataException;
import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.model.User;
import com.project.medical_clinic_system.repository.UserRepository;
import com.project.medical_clinic_system.security.JwtService;
import com.project.medical_clinic_system.service.AdminService;
import com.project.medical_clinic_system.service.AuthService;
import com.project.medical_clinic_system.service.DoctorService;
import com.project.medical_clinic_system.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final UUID SPECIALIZATION_ID = UUID.randomUUID();

    @Mock
    private UserRepository userRepository;
    @Mock
    private DoctorService doctorService;
    @Mock
    private PatientService patientService;
    @Mock
    private AdminService adminService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private Claims claims;

    @InjectMocks
    private AuthService authService;

    private User user() {
        return new Admin("Admin One", "admin@mail.com", "encoded-pass", "01012345678");
    }

    // =====================================================================
    // loginUser (single active session per account)
    // =====================================================================

    private void stubSuccessfulCredentials(User user) {
        when(userRepository.findByEmailForUpdate(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Passw0rd@1", "encoded-pass")).thenReturn(true);
    }

    private void stubTokenIssuing(User user, Instant expiresAt) {
        when(jwtService.generateToken(eq(user), anyString())).thenReturn("jwt-token");
        when(jwtService.extractClaims("jwt-token")).thenReturn(claims);
        when(claims.getExpiration()).thenReturn(Date.from(expiresAt));
    }

    @Test
    void loginUser_validCredentials_returnsTokenAndUserInfo() {
        User user = user();
        UUID id = UUID.randomUUID();
        user.setId(id);
        stubSuccessfulCredentials(user);
        stubTokenIssuing(user, Instant.now().plusSeconds(900));

        LoginResponse response = authService.loginUser(new LoginRequest("admin@mail.com", "Passw0rd@1"));

        assertEquals("jwt-token", response.getToken());
        assertEquals(id, response.getId());
        assertEquals("Admin One", response.getName());
        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void loginUser_success_storesSessionIdAndExpiryOnUser_andSavesIt() {
        User user = user();
        Instant expiresAt = Instant.now().plusSeconds(900).truncatedTo(ChronoUnit.MILLIS);
        stubSuccessfulCredentials(user);
        stubTokenIssuing(user, expiresAt);

        authService.loginUser(new LoginRequest("admin@mail.com", "Passw0rd@1"));

        ArgumentCaptor<String> sessionCaptor = ArgumentCaptor.forClass(String.class);
        verify(jwtService).generateToken(eq(user), sessionCaptor.capture());
        // the same session id that went into the token must be the one stored on the user
        assertNotNull(sessionCaptor.getValue());
        assertEquals(sessionCaptor.getValue(), user.getActiveSessionId());
        assertEquals(expiresAt, user.getActiveSessionExpiresAt());
        verify(userRepository).save(user);
    }

    @Test
    void loginUser_lockedLookup_usesFindByEmailForUpdate() {
        User user = user();
        stubSuccessfulCredentials(user);
        stubTokenIssuing(user, Instant.now().plusSeconds(900));

        authService.loginUser(new LoginRequest("admin@mail.com", "Passw0rd@1"));

        verify(userRepository).findByEmailForUpdate("admin@mail.com");
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void loginUser_accountAlreadyLoggedIn_throwsActiveSession_andIssuesNoToken() {
        User user = user();
        user.setActiveSessionId("existing-session");
        user.setActiveSessionExpiresAt(Instant.now().plusSeconds(600));
        stubSuccessfulCredentials(user);

        ActiveSessionException ex = assertThrows(ActiveSessionException.class,
                () -> authService.loginUser(new LoginRequest("admin@mail.com", "Passw0rd@1")));

        assertEquals("This account is already logged in", ex.getMessage());
        verify(jwtService, never()).generateToken(any(), any());
        verify(userRepository, never()).save(any());
        assertEquals("existing-session", user.getActiveSessionId());
    }

    @Test
    void loginUser_previousSessionExpired_allowsNewLoginWithNewSessionId() {
        User user = user();
        user.setActiveSessionId("old-session");
        user.setActiveSessionExpiresAt(Instant.now().minusSeconds(60));
        stubSuccessfulCredentials(user);
        stubTokenIssuing(user, Instant.now().plusSeconds(900));

        LoginResponse response = authService.loginUser(new LoginRequest("admin@mail.com", "Passw0rd@1"));

        assertEquals("jwt-token", response.getToken());
        assertNotEquals("old-session", user.getActiveSessionId());
        verify(userRepository).save(user);
    }

    @Test
    void loginUser_sessionIdWithoutExpiry_isNotTreatedAsActive() {
        User user = user();
        user.setActiveSessionId("half-written-session");
        user.setActiveSessionExpiresAt(null);
        stubSuccessfulCredentials(user);
        stubTokenIssuing(user, Instant.now().plusSeconds(900));

        assertDoesNotThrow(() -> authService.loginUser(new LoginRequest("admin@mail.com", "Passw0rd@1")));
    }

    @Test
    void loginUser_eachLoginGetsADifferentSessionId() {
        User first = user();
        User second = user();
        stubSuccessfulCredentials(first);
        when(userRepository.findByEmailForUpdate("admin@mail.com"))
                .thenReturn(Optional.of(first)).thenReturn(Optional.of(second));
        when(jwtService.generateToken(any(User.class), anyString())).thenReturn("jwt-token");
        when(jwtService.extractClaims("jwt-token")).thenReturn(claims);
        when(claims.getExpiration()).thenReturn(Date.from(Instant.now().plusSeconds(900)));

        authService.loginUser(new LoginRequest("admin@mail.com", "Passw0rd@1"));
        authService.loginUser(new LoginRequest("admin@mail.com", "Passw0rd@1"));

        assertNotEquals(first.getActiveSessionId(), second.getActiveSessionId());
    }

    @Test
    void loginUser_unknownEmail_throwsInvalidData() {
        when(userRepository.findByEmailForUpdate("nobody@mail.com")).thenReturn(Optional.empty());

        InvalidDataException ex = assertThrows(InvalidDataException.class,
                () -> authService.loginUser(new LoginRequest("nobody@mail.com", "Passw0rd@1")));

        assertEquals("Invalid email or password", ex.getMessage());
        verify(jwtService, never()).generateToken(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUser_wrongPassword_throwsInvalidData_andDoesNotTouchSession() {
        User user = user();
        when(userRepository.findByEmailForUpdate("admin@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Wrong@123", "encoded-pass")).thenReturn(false);

        InvalidDataException ex = assertThrows(InvalidDataException.class,
                () -> authService.loginUser(new LoginRequest("admin@mail.com", "Wrong@123")));

        // same message as unknown email, so attackers can't tell which one was wrong
        assertEquals("Invalid email or password", ex.getMessage());
        assertNull(user.getActiveSessionId());
        verify(jwtService, never()).generateToken(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUser_wrongPasswordOnLoggedInAccount_reportsInvalidCredentials_notActiveSession() {
        // the password is checked BEFORE the session, so a stranger can't learn the account is online
        User user = user();
        user.setActiveSessionId("existing-session");
        user.setActiveSessionExpiresAt(Instant.now().plusSeconds(600));
        when(userRepository.findByEmailForUpdate("admin@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Wrong@123", "encoded-pass")).thenReturn(false);

        assertThrows(InvalidDataException.class,
                () -> authService.loginUser(new LoginRequest("admin@mail.com", "Wrong@123")));
    }

    // =====================================================================
    // logout
    // =====================================================================

    @Test
    void logout_clearsSessionFieldsAndSaves() {
        User user = user();
        user.setActiveSessionId("session-1");
        user.setActiveSessionExpiresAt(Instant.now().plusSeconds(600));
        when(userRepository.findByEmailForUpdate("admin@mail.com")).thenReturn(Optional.of(user));

        authService.logout("admin@mail.com");

        assertNull(user.getActiveSessionId());
        assertNull(user.getActiveSessionExpiresAt());
        verify(userRepository).save(user);
    }

    @Test
    void logout_whenNoActiveSession_stillSucceeds() {
        User user = user();
        when(userRepository.findByEmailForUpdate("admin@mail.com")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> authService.logout("admin@mail.com"));
        verify(userRepository).save(user);
    }

    @Test
    void logout_unknownUser_throwsInvalidData() {
        when(userRepository.findByEmailForUpdate("ghost@mail.com")).thenReturn(Optional.empty());

        InvalidDataException ex = assertThrows(InvalidDataException.class,
                () -> authService.logout("ghost@mail.com"));

        assertEquals("User not found", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginAfterLogout_isAllowed() {
        User user = user();
        user.setActiveSessionId("session-1");
        user.setActiveSessionExpiresAt(Instant.now().plusSeconds(600));
        when(userRepository.findByEmailForUpdate("admin@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Passw0rd@1", "encoded-pass")).thenReturn(true);
        stubTokenIssuing(user, Instant.now().plusSeconds(900));

        authService.logout("admin@mail.com");

        assertDoesNotThrow(() -> authService.loginUser(new LoginRequest("admin@mail.com", "Passw0rd@1")));
        assertNotNull(user.getActiveSessionId());
    }

    // =====================================================================
    // registerUser
    // =====================================================================

    private RegisterRequest registerRequest(Role role) {
        return new RegisterRequest(
                "Ahmed Mohamed Ali", "ahmed@mail.com", "Passw0rd@1", "01012345678", role,
                LocalDate.of(1995, 5, 20), Gender.MALE, LocalDate.of(2026, 1, 1),
                "1234-5678", 8, new BigDecimal("300"), SPECIALIZATION_ID,
                "admin-secret");
    }

    @Test
    void registerUser_patient_delegatesToPatientService() {
        PatientResponse expected = new PatientResponse(UUID.randomUUID(), "Ahmed Mohamed Ali",
                "ahmed@mail.com", LocalDate.of(2026, 1, 1).atStartOfDay());
        when(patientService.createPatient(any(CreatePatientRequest.class))).thenReturn(expected);

        Object result = authService.registerUser(registerRequest(Role.PATIENT));

        ArgumentCaptor<CreatePatientRequest> captor = ArgumentCaptor.forClass(CreatePatientRequest.class);
        verify(patientService).createPatient(captor.capture());
        CreatePatientRequest sent = captor.getValue();

        assertSame(expected, result);
        assertEquals("Ahmed Mohamed Ali", sent.getName());
        assertEquals("ahmed@mail.com", sent.getEmail());
        assertEquals("Passw0rd@1", sent.getPassword());
        assertEquals("01012345678", sent.getPhone());
        assertEquals(LocalDate.of(1995, 5, 20), sent.getDateOfBirth());
        assertEquals(Gender.MALE, sent.getGender());
        assertEquals(LocalDate.of(2026, 1, 1).atStartOfDay(), sent.getRegistrationDate());
        verifyNoInteractions(doctorService, adminService);
    }

    @Test
    void registerUser_doctor_delegatesToDoctorService() {
        DoctorResponse expected = new DoctorResponse(UUID.randomUUID(), "Ahmed Mohamed Ali", "ahmed@mail.com", "Cardiology");
        when(doctorService.createDoctor(any(CreateDoctorRequest.class))).thenReturn(expected);

        Object result = authService.registerUser(registerRequest(Role.DOCTOR));

        ArgumentCaptor<CreateDoctorRequest> captor = ArgumentCaptor.forClass(CreateDoctorRequest.class);
        verify(doctorService).createDoctor(captor.capture());
        CreateDoctorRequest sent = captor.getValue();

        assertSame(expected, result);
        assertEquals("ahmed@mail.com", sent.getEmail());
        assertEquals("Passw0rd@1", sent.getPassword());
        assertEquals("1234-5678", sent.getLicenseNumber());
        assertEquals(8, sent.getYearsOfExperience());
        assertEquals(new BigDecimal("300"), sent.getConsultationFee());
        assertEquals(SPECIALIZATION_ID, sent.getSpecializationID());
        verifyNoInteractions(patientService, adminService);
    }

    @Test
    void registerUser_admin_delegatesToAdminService() {
        AdminResponse expected = new AdminResponse(UUID.randomUUID(), "Ahmed Mohamed Ali", "ahmed@mail.com");
        when(adminService.createAdmin(any(CreateAdminRequest.class))).thenReturn(expected);

        Object result = authService.registerUser(registerRequest(Role.ADMIN));

        ArgumentCaptor<CreateAdminRequest> captor = ArgumentCaptor.forClass(CreateAdminRequest.class);
        verify(adminService).createAdmin(captor.capture());
        CreateAdminRequest sent = captor.getValue();

        assertSame(expected, result);
        assertEquals("ahmed@mail.com", sent.getEmail());
        assertEquals("Passw0rd@1", sent.getPassword());
        assertEquals("admin-secret", sent.getAdminPassword());
        verifyNoInteractions(patientService, doctorService);
    }

    @Test
    void registerUser_nullRole_throwsInvalidDataAndCallsNoService() {
        InvalidDataException ex = assertThrows(InvalidDataException.class,
                () -> authService.registerUser(registerRequest(null)));

        assertEquals("Role is required", ex.getMessage());
        verifyNoInteractions(patientService, doctorService, adminService);
    }
}
