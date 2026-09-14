package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateAvailabilityRequest;
import com.project.medical_clinic_system.dto.response.AvailabilityResponse;
import com.project.medical_clinic_system.service.AvailabilityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public AvailabilityResponse createAvailability(@RequestBody CreateAvailabilityRequest request) {
        return availabilityService.createAvailability(request);
    }

    @GetMapping("/{availabilityID}")
    public AvailabilityResponse getAvailabilityByID(@PathVariable UUID availabilityID) {
        return availabilityService.findAvailabilityByID(availabilityID);
    }

    @GetMapping
    public List<AvailabilityResponse> getAllAvailability() {
        return availabilityService.findAllAvailability();
    }

    @DeleteMapping("/{availabilityID}")
    public String deleteAvailability(@PathVariable UUID availabilityID) {
        return availabilityService.deleteAvailabilityByID(availabilityID);
    }
}