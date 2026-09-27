package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateAdminRequest;
import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @PostMapping
    public AdminResponse CreateAdmin(@RequestBody CreateAdminRequest request){
        return adminService.createAdmin(request);
    }
    @DeleteMapping("{adminID}")
    public String deleteAdmin(@PathVariable(name = "adminID") UUID adminID ){
        return adminService.deleteById(adminID);
    }

}
