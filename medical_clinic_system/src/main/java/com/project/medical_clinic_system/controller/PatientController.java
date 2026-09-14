package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreatePatientRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.service.AppointmentService;
import com.project.medical_clinic_system.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientController(PatientService patientService,
                             AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public PatientResponse createPatient(
            @RequestBody CreatePatientRequest request) {
        return patientService.createPatient(request);
    }

    @GetMapping("/{patientID}")
    public PatientResponse getPatientByID(
            @PathVariable(name = "patientID") UUID patientID) {
        return patientService.findPatientByID(patientID);
    }

    @GetMapping
    public List<PatientResponse> findAllPatient() {
        return patientService.findAllPatient();
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

    @GetMapping("/{patientID}/appointments")
    public List<AppointmentResponse> findPatientAppointments(
            @PathVariable(name = "patientID") UUID patientID) {
        return appointmentService.findPatientAppointments(patientID);
    }
}