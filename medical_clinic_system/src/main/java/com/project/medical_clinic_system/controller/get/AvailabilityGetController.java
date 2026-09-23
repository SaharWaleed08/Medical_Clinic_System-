package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.AvailabilityResponse;
import com.project.medical_clinic_system.service.AvailabilityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityGetController {

    private final AvailabilityService availabilityService;

    public AvailabilityGetController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{availabilityID}")
    public AvailabilityResponse getAvailabilityByID(@PathVariable UUID availabilityID) {
        return availabilityService.findAvailabilityByID(availabilityID);
    }

    @GetMapping
    public List<AvailabilityResponse> getAllAvailability() {
        return availabilityService.findAllAvailability();
    }
}