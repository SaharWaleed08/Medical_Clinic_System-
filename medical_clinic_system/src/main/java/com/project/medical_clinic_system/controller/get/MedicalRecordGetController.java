package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.service.MedicalRecordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.project.medical_clinic_system.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordGetController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordGetController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PreAuthorize("@medicalRecordRepository.findById(#medicalRecordId).orElse(null)?.doctorID == authentication.principal.id or @medicalRecordRepository.findById(#medicalRecordId).orElse(null)?.patientID == authentication.principal.id ")
    @GetMapping("/{medicalRecordId}")
    public MedicalRecordResponse getMedicalRecordById(
            @PathVariable(name = "medicalRecordId") UUID medicalRecordId) {
        return medicalRecordService.findById(medicalRecordId);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping
    public List<MedicalRecordResponse> getAll(@AuthenticationPrincipal User doctor) {
        return medicalRecordService.findByDoctorID(doctor.getId());
    }
}