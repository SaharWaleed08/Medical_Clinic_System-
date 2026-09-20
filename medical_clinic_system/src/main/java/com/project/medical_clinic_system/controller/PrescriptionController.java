package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreatePrescriptionRequest;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.service.PrescriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public PrescriptionResponse createPrescription(@RequestBody CreatePrescriptionRequest request) {
        return prescriptionService.createPrescription(request);
    }
    @GetMapping("/{prescriptionId}")
    public PrescriptionResponse getById(@PathVariable(name = "prescriptionId") UUID prescriptionID){
        return prescriptionService.findPrescriptionById(prescriptionID);
    }
    @GetMapping
    public List<PrescriptionResponse> getAll(){
        return prescriptionService.findAll();
    }
    @DeleteMapping("/{prescriptionId}")
    public String  deleteById(@PathVariable(name = "prescriptionId") UUID prescriptionID){
        return prescriptionService.deletePrescriptionById(prescriptionID);
    }
}
