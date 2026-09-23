package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreatePatientRequest;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public PatientResponse createPatient(
            @RequestBody CreatePatientRequest request) {
        return patientService.createPatient(request);
    }

    @PutMapping("/{patientID}")
    public PatientResponse updatePatientByID(
            @PathVariable(name = "patientID") UUID patientID,
            @RequestBody CreatePatientRequest request) {
        return patientService.updatePatientByID(patientID, request);
    }

    @DeleteMapping("/{patientID}")
    public String deletePatientByID(
            @PathVariable(name = "patientID") UUID patientID) {
        return patientService.deletePatientByID(patientID);
    }
}