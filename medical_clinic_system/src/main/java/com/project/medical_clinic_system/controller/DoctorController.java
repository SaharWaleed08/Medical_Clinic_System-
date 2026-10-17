package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public DoctorResponse createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        return doctorService.createDoctor(request);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and #doctorID == authentication.principal.id)")
    @PutMapping("/{doctorID}")
    public DoctorResponse updateDoctorByID(
            @PathVariable(name = "doctorID") UUID doctorID,@Valid
            @RequestBody CreateDoctorRequest request) {
        return doctorService.updateDoctorByID(doctorID, request);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{doctorID}")
    public String deleteDoctorByID(
            @PathVariable(name = "doctorID") UUID doctorID) {
        return doctorService.deleteDoctorByID(doctorID);
    }

}