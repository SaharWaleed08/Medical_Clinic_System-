package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.service.MedicalRecordService;
import com.project.medical_clinic_system.service.PrescriptionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * NEW file: controller/get/PatientRecordsController.java
 *
 * Lets a patient list THEIR OWN medical records and prescriptions
 * (admin and doctors can read any patient's).
 * Same security rule as GET /api/patients/{patientID}/appointments.
 */
@RestController
@RequestMapping("/api/patients/{patientID}")
public class PatientRecordsController {

    private final MedicalRecordService medicalRecordService;
    private final PrescriptionService prescriptionService;

    public PatientRecordsController(MedicalRecordService medicalRecordService,
                                    PrescriptionService prescriptionService) {
        this.medicalRecordService = medicalRecordService;
        this.prescriptionService = prescriptionService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or (isAuthenticated() and #patientID == authentication.principal.id)")
    @GetMapping("/medical-records")
    public List<MedicalRecordResponse> getPatientRecords(@PathVariable(name = "patientID") UUID patientID) {
        return medicalRecordService.findByPatientID(patientID);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or (isAuthenticated() and #patientID == authentication.principal.id)")
    @GetMapping("/prescriptions")
    public List<PrescriptionResponse> getPatientPrescriptions(@PathVariable(name = "patientID") UUID patientID) {
        return prescriptionService.findByPatientID(patientID);
    }
}
