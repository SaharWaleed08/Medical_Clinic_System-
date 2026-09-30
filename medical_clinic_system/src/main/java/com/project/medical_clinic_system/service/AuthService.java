package com.project.medical_clinic_system.service;


import com.project.medical_clinic_system.dto.request.*;
import com.project.medical_clinic_system.dto.response.LoginResponse;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.exception.InvalidDataException;
import com.project.medical_clinic_system.model.User;
import com.project.medical_clinic_system.repository.UserRepository;
import com.project.medical_clinic_system.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidDataException(" email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidDataException(user.getPassword() + " and " + request.getPassword());
        }
        String token = jwtService.generateToken(user);
        return new LoginResponse(token);
    }

    public Object registerUser(RegisterRequest request) {

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
                            request.consultationFee()
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

}
