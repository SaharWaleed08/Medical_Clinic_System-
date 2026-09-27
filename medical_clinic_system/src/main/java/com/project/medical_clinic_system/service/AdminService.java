package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateAdminRequest;
import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.exception.InvalidAdminRegistrationPasswordException;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.AdminMapper;
import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService {
    @Value("${admin.registration-password}")
    private String adminPassword;
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    public AdminService(AdminRepository adminRepository, AdminMapper adminMapper) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
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

    public AdminResponse findById(UUID adminID) {
        Admin admin = adminRepository.findById(adminID)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return adminMapper.toResponse(admin);
    }

    public List<AdminResponse> findAll() {
        List<Admin> admins = adminRepository.findAll();
        return adminMapper.toResponse(admins);
    }

    public String deleteById(UUID adminID) {
        adminRepository.deleteById(adminID);
        return "Admin is deleted";
    }

}
