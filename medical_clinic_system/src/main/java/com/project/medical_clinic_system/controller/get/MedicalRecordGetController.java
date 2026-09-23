package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.service.MedicalRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medicalRecords")
public class MedicalRecordGetController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordGetController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/{medicalRecordId}")
    public MedicalRecordResponse getMedicalRecordById(
            @PathVariable(name = "medicalRecordId") UUID medicalRecordId) {
        return medicalRecordService.findById(medicalRecordId);
    }

    @GetMapping
    public List<MedicalRecordResponse> getAll() {
        return medicalRecordService.findAll();
    }
}