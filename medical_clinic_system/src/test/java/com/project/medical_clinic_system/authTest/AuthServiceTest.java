package com.project.medical_clinic_system.authTest;

import com.project.medical_clinic_system.dto.request.*;
import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.dto.response.LoginResponse;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.exception.InvalidDataException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

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

    @InjectMocks
    private AuthService authService;

    private User user() {
        return new Admin("Admin One", "admin@mail.com", "encoded-pass", "01012345678");
    }

    // =====================================================================
    // loginUser
    // =====================================================================

    @Test
    void loginUser_validCredentials_returnsToken() {
        User user = user();
        LoginRequest request = new LoginRequest("admin@mail.com", "Passw0rd@1");
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Passw0rd@1", "encoded-pass")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.loginUser(request);

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginUser_unknownEmail_throwsInvalidData() {
        when(userRepository.findByEmail("nobody@mail.com")).thenReturn(Optional.empty());

        InvalidDataException ex = assertThrows(InvalidDataException.class,
                () -> authService.loginUser(new LoginRequest("nobody@mail.com", "Passw0rd@1")));

        assertEquals("invalid email or password", ex.getMessage());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void loginUser_wrongPassword_throwsInvalidDataAndNoToken() {
        User user = user();
        when(userRepository.findByEmail("admin@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Wrong@123", "encoded-pass")).thenReturn(false);

        InvalidDataException ex = assertThrows(InvalidDataException.class,
                () -> authService.loginUser(new LoginRequest("admin@mail.com", "Wrong@123")));

        // same message for both cases so attackers can't tell which one was wrong
        assertEquals("invalid email or password", ex.getMessage());
        verify(jwtService, never()).generateToken(any());
    }

    // =====================================================================
    // registerUser
    // =====================================================================

    private RegisterRequest registerRequest(Role role) {
        return new RegisterRequest(
                "Ahmed Mohamed Ali", "ahmed@mail.com", "Passw0rd@1", "01012345678", role,
                LocalDate.of(1995, 5, 20), Gender.MALE, LocalDate.of(2026, 1, 1),
                "1234-5678", 8, new BigDecimal("300"),
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
        DoctorResponse expected = new DoctorResponse(UUID.randomUUID(), "Ahmed Mohamed Ali", "ahmed@mail.com");
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
    void registerUser_nullRole_returnsNullAndCallsNoService() {
        assertNull(authService.registerUser(registerRequest(null)));

        verifyNoInteractions(patientService, doctorService, adminService);
    }
}
