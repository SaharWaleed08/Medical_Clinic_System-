package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/admins")
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
    public Page<AdminResponse> getAdmins(@RequestParam(required = false) String name,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "name") String sortBy,
                                         @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        return adminService.findAll(name,pageable);
    }
}
