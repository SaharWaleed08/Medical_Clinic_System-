package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateMedicalRecordRequest;
import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.service.MedicalRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medicalRecords")
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }
    @PostMapping
    public MedicalRecordResponse CreateMedicalRecord(@RequestBody CreateMedicalRecordRequest request){
        return medicalRecordService.CreateMedicalRecord(request);
    }
    @GetMapping("/{medicalRecordId}")
    public MedicalRecordResponse getMedicalRecordById(@PathVariable(name = "medicalRecordId") UUID medicalRecordId){
        return medicalRecordService.findById(medicalRecordId);
    }
    @GetMapping
    public List<MedicalRecordResponse> getAll(){
        return medicalRecordService.findAll();
    }
    @DeleteMapping("/{medicalRecordId}")
    public String  deleteMedicalRecordById(@PathVariable(name = "medicalRecordId") UUID medicalRecordId){
        return medicalRecordService.deleteMedicalRecordById(medicalRecordId);
    }
}
