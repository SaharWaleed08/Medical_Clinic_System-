package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateAvailabilityRequest;
import com.project.medical_clinic_system.dto.response.AvailabilityResponse;
import com.project.medical_clinic_system.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (isAuthenticated() and #request.doctorID == authentication.principal.id)")
    @PostMapping
    public AvailabilityResponse createAvailability(@Valid @RequestBody CreateAvailabilityRequest request) {
        return availabilityService.createAvailability(request);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (isAuthenticated() and @availabilityRepository.findById(#availabilityID).orElse(null)?.doctor?.id == authentication.principal.id)")
    @DeleteMapping("/{availabilityID}")
    public String deleteAvailability(@PathVariable UUID availabilityID) {
        return availabilityService.deleteAvailabilityByID(availabilityID);
    }
}