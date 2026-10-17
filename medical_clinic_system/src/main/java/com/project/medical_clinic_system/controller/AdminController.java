package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateAdminRequest;
import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public AdminResponse CreateAdmin(@Valid @RequestBody CreateAdminRequest request) {
        return adminService.createAdmin(request);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{adminID}")
    public AdminResponse updateAdmin(@PathVariable(name = "adminID") UUID adminID, @Valid @RequestBody CreateAdminRequest request) {
        return adminService.updateById(adminID, request);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') and #adminID != authentication.principal.id")
    @DeleteMapping("{adminID}")
    public String deleteAdmin(@PathVariable(name = "adminID") UUID adminID) {
        return adminService.deleteById(adminID);
    }

}
