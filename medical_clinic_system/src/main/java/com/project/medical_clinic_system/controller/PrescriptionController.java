package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreatePrescriptionRequest;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.project.medical_clinic_system.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.UUID;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping
    public PrescriptionResponse createPrescription(
            @Valid @RequestBody CreatePrescriptionRequest request,
            @AuthenticationPrincipal User doctor) {
        return prescriptionService.createPrescription(request, doctor.getId());
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @DeleteMapping("/{prescriptionId}")
    public String deleteById(
            @PathVariable(name = "prescriptionId") UUID prescriptionID) {
        return prescriptionService.deletePrescriptionById(prescriptionID);
    }
}