package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.service.PrescriptionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.project.medical_clinic_system.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionGetController {

    private final PrescriptionService prescriptionService;

    public PrescriptionGetController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PreAuthorize("@prescriptionRepository.findById(#prescriptionID).orElse(null)?.medicalVisitRecord?.doctorId == authentication.principal.id or @medicalRecordRepository.findById(#medicalRecordId).orElse(null)?.medicalVisitRecord?.patientId == authentication.principal.id ")
    @GetMapping("/{prescriptionID}")
    public PrescriptionResponse getById(
            @PathVariable(name = "prescriptionID") UUID prescriptionID) {
        return prescriptionService.findPrescriptionById(prescriptionID);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping
    public List<PrescriptionResponse> getAll(@AuthenticationPrincipal User doctor) {
        return prescriptionService.findByDoctorID(doctor.getId());
    }
}