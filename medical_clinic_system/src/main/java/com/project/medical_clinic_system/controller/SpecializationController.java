package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateSpecializationRequest;
import com.project.medical_clinic_system.dto.response.SpecializationResponse;
import com.project.medical_clinic_system.service.SpecializationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/specialization")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public SpecializationResponse createSpecialization(
            @Valid @RequestBody CreateSpecializationRequest request) {
        return specializationService.createSpecialization(request);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{specializationID}")
    public SpecializationResponse updateSpecializationByID(
            @PathVariable UUID specializationID,
            @Valid @RequestBody CreateSpecializationRequest request) {
        return specializationService.updateSpecializationByID(specializationID, request);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{specializationID}")
    public String deleteSpecializationByID(
            @PathVariable UUID specializationID) {
        return specializationService.deleteSpecializationByID(specializationID);
    }
}