package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateAdminRequest;
import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.exception.InvalidAdminRegistrationPasswordException;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.AdminMapper;
import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.repository.AdminRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class AdminService {
    @Value("${admin.registration-password}")
    private String adminPassword;
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminService(String adminPassword, AdminRepository adminRepository, AdminMapper adminMapper, PasswordEncoder passwordEncoder) {
        this.adminPassword = adminPassword;
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AdminResponse createAdmin(CreateAdminRequest request) {
        if (!adminPassword.equals(request.getAdminPassword())) {
            throw new InvalidAdminRegistrationPasswordException(
                    "Invalid admin registration password"
            );
        }


        Admin admin = new Admin(request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getPhone());
        adminRepository.save(admin);

        return adminMapper.toResponse(admin);
    }

    public AdminResponse findById(UUID adminID) {
        Admin admin = adminRepository.findById(adminID)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return adminMapper.toResponse(admin);
    }

    public Page<AdminResponse> findAll(String name, Pageable pageable) {

        Page<Admin> admins;

        if (name == null || name.isBlank()) {
            admins = adminRepository.findAll(pageable);
        } else {
            admins = adminRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        return admins.map(admin -> new AdminResponse(admin.getId(), admin.getName(), admin.getEmail()));

    }

    public AdminResponse updateById(UUID adminId, CreateAdminRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setPhone(request.getPhone());

        return adminMapper.toResponse(admin);
    }

    public String deleteById(UUID adminID) {
        adminRepository.deleteById(adminID);
        return "Admin is deleted";
    }

}
