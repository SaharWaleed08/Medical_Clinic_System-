package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminGetController {
    private final AdminService adminService;

    public AdminGetController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("{adminID}")
    public AdminResponse getAdmin(@PathVariable(name = "adminID") UUID adminID) {
        return adminService.findById(adminID);
    }

    @GetMapping
    public List<AdminResponse> getAdmins() {
        return adminService.findAll();
    }
}
