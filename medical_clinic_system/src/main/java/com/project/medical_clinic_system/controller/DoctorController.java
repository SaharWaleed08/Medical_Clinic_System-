package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.service.AppointmentService;
import com.project.medical_clinic_system.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService,
                            AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public DoctorResponse createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        return doctorService.createDoctor(request);
    }

    @PutMapping("/{doctorID}")
    public DoctorResponse updateDoctorByID(
            @PathVariable(name = "doctorID") UUID doctorID,@Valid
            @RequestBody CreateDoctorRequest request) {
        return doctorService.updateDoctorByID(doctorID, request);
    }

    @DeleteMapping("/{doctorID}")
    public String deleteDoctorByID(
            @PathVariable(name = "doctorID") UUID doctorID) {
        return doctorService.deleteDoctorByID(doctorID);
    }

}