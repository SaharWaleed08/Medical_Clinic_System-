package com.project.medical_clinic_system.service;


import com.project.medical_clinic_system.dto.request.*;
import com.project.medical_clinic_system.dto.response.LoginResponse;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.exception.InvalidData;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.model.User;
import com.project.medical_clinic_system.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AdminService adminService;


    public AuthService(UserRepository userRepository, DoctorService doctorService, PatientService patientService, AdminService adminService) {
        this.userRepository = userRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.adminService = adminService;
    }




    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!request.getPassword().equals(user.getPassword())) {
            throw new InvalidData("invalid email or password");
        }
        return new LoginResponse(user.getId(), user.getName());
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

            return doctorService.createDoctor(doctorRequest) ;
        }
        if (request.role()==Role.ADMIN){
            CreateAdminRequest adminRequest=new CreateAdminRequest(
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
