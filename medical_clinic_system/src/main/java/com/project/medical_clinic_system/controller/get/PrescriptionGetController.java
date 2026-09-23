package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.service.PrescriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionGetController {

    private final PrescriptionService prescriptionService;

    public PrescriptionGetController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping("/{prescriptionId}")
    public PrescriptionResponse getById(
            @PathVariable(name = "prescriptionId") UUID prescriptionID) {
        return prescriptionService.findPrescriptionById(prescriptionID);
    }

    @GetMapping
    public List<PrescriptionResponse> getAll() {
        return prescriptionService.findAll();
    }
}