package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateMedicalRecordRequest;
import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/medical-record")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public MedicalRecordResponse CreateMedicalRecord(
            @Valid @RequestBody CreateMedicalRecordRequest request) {
        return medicalRecordService.CreateMedicalRecord(request);
    }

    @DeleteMapping("/{medicalRecordId}")
    public String deleteMedicalRecordById(
            @PathVariable(name = "medicalRecordId") UUID medicalRecordId) {
        return medicalRecordService.deleteMedicalRecordById(medicalRecordId);
    }
}