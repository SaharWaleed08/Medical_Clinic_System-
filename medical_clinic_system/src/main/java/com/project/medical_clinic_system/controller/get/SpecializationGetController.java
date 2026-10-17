package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.SpecializationResponse;
import com.project.medical_clinic_system.service.SpecializationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationGetController {

    private final SpecializationService specializationService;

    public SpecializationGetController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{specializationID}")
    public SpecializationResponse getSpecializationByID(@PathVariable UUID specializationID) {
        return specializationService.findSpecializationByID(specializationID);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<SpecializationResponse> findAllSpecialization() {
        return specializationService.findAllSpecialization();
    }
}