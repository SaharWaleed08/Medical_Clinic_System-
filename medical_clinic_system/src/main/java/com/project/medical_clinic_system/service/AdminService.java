package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateAdminRequest;
import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.exception.InvalidAdminRegistrationPasswordException;
import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Value("${admin.registration-password}")
    private String adminPassword;
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AdminResponse createAdmin(CreateAdminRequest request) {
        if (!adminPassword.equals(request.getAdminPassword())) {
            throw new InvalidAdminRegistrationPasswordException(
                    "Invalid admin registration password"
            );
        }
        Admin admin = new Admin(request.getName(), request.getEmail(), request.getPassword(), request.getPhone());
        adminRepository.save(admin);

        return new AdminResponse(admin.getId(), admin.getName(), admin.getEmail());
    }

}
