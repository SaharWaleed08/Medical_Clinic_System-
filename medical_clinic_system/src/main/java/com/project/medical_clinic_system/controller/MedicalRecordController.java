package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateMedicalRecordRequest;
import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.service.MedicalRecordService;
import com.project.medical_clinic_system.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/medical-record")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping
    public MedicalRecordResponse CreateMedicalRecord(
            @Valid @RequestBody CreateMedicalRecordRequest request,
            @AuthenticationPrincipal User doctor) {
        return medicalRecordService.CreateMedicalRecord(request, doctor.getId());
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')" )
    @DeleteMapping("/{medicalRecordId}")
    public String deleteMedicalRecordById(
            @PathVariable(name = "medicalRecordId") UUID medicalRecordId) {
        return medicalRecordService.deleteMedicalRecordById(medicalRecordId);
    }
}