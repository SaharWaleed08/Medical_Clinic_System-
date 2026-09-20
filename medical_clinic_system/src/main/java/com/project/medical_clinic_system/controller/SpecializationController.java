package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateSpecializationRequest;
import com.project.medical_clinic_system.dto.response.SpecializationResponse;
import com.project.medical_clinic_system.service.SpecializationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/specializations")
public class SpecializationController {
    private final SpecializationService specializationService;

    public SpecializationController(SpecializationService specializationService) {
        this.specializationService = specializationService;
    }
    @PostMapping
    public SpecializationResponse createSpecialization(@RequestBody CreateSpecializationRequest request) {
        return specializationService.createSpecialization(request);
    }

    @GetMapping("/{specializationID}")
    public SpecializationResponse getSpecializationByID(@PathVariable UUID specializationID) {
        return specializationService.findSpecializationByID(specializationID);
    }

    @GetMapping
    public List<SpecializationResponse> findAllSpecialization() {
        return specializationService.findAllSpecialization();
    }

    @PutMapping("/{specializationID}")
    public SpecializationResponse updateSpecializationByID(@PathVariable UUID specializationID, @RequestBody CreateSpecializationRequest request) {
        return specializationService.updateSpecializationByID(specializationID, request);
    }
    @DeleteMapping("/{specializationID}")
    public String deleteSpecializationByID(@PathVariable UUID specializationID){
        return specializationService.deleteSpecializationByID(specializationID);
    }
}
