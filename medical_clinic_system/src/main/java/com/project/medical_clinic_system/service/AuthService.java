package com.project.medical_clinic_system.service;


import com.project.medical_clinic_system.dto.request.*;
import com.project.medical_clinic_system.dto.response.LoginResponse;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.exception.ActiveSessionException;
import com.project.medical_clinic_system.exception.InvalidDataException;
import com.project.medical_clinic_system.model.User;
import com.project.medical_clinic_system.repository.UserRepository;
import com.project.medical_clinic_system.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthService(UserRepository userRepository, DoctorService doctorService, PatientService patientService, AdminService adminService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    public LoginResponse loginUser(LoginRequest request) {

        User user = userRepository.findByEmailForUpdate(request.getEmail())
                .orElseThrow(() ->
                        new InvalidDataException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            throw new InvalidDataException("Invalid email or password");
        }

        Instant now = Instant.now();

        if (user.getActiveSessionId() != null
                && user.getActiveSessionExpiresAt() != null
                && user.getActiveSessionExpiresAt().isAfter(now)) {

            throw new ActiveSessionException(
                    "This account is already logged in"
            );
        }

        String sessionId = UUID.randomUUID().toString();

        String token = jwtService.generateToken(user, sessionId);

        Instant tokenExpiration = jwtService
                .extractClaims(token)
                .getExpiration()
                .toInstant();

        user.setActiveSessionId(sessionId);
        user.setActiveSessionExpiresAt(tokenExpiration);

        userRepository.save(user);

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getRole(),
                token
        );
    }

    public Object registerUser(RegisterRequest request) {
        if (request.role() == null) {
            throw new InvalidDataException("Role is required");
        }

        if (request.role() == Role.PATIENT) {

            CreatePatientRequest patientRequest =
                    new CreatePatientRequest(
                            request.name(),
                            request.password(),
                            request.email(),
                            request.phone(),
                            request.dateOfBirth(),
                            request.gender(),
                            request.registrationDate().atStartOfDay()
                    );

            return patientService.createPatient(patientRequest);
        }

        if (request.role() == Role.DOCTOR) {

            CreateDoctorRequest doctorRequest =
                    new CreateDoctorRequest(
                            request.name(),
                            request.email(),
                            request.password(),
                            request.phone(),
                            request.licenseNumber(),
                            request.yearsOfExperience(),
                            request.consultationFee(),
                            request.specializationID()
                    );

            return doctorService.createDoctor(doctorRequest);
        }
        if (request.role() == Role.ADMIN) {
            CreateAdminRequest adminRequest = new CreateAdminRequest(
                    request.name(),
                    request.password(),
                    request.email(),
                    request.phone(),
                    request.adminPassword()
            );
            return adminService.createAdmin(adminRequest);
        }


        return null;
    }

    public void logout(String email) {

        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() ->
                        new InvalidDataException("User not found"));

        user.setActiveSessionId(null);
        user.setActiveSessionExpiresAt(null);

        userRepository.save(user);
    }

}
