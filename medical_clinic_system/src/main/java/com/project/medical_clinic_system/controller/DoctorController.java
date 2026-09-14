package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.service.AppointmentService;
import com.project.medical_clinic_system.service.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService,
                            AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public DoctorResponse createDoctor(@RequestBody CreateDoctorRequest request) {
        return doctorService.createDoctor(request);
    }

    @GetMapping("/{doctorID}")
    public DoctorResponse getDoctorByID(
            @PathVariable(name = "doctorID") UUID doctorID) {
        return doctorService.findDoctorByID(doctorID);
    }

    @GetMapping
    public List<DoctorResponse> findAllDoctor() {
        return doctorService.findAllDoctor();
    }

    @PutMapping("/{doctorID}")
    public DoctorResponse updateDoctorByID(
            @PathVariable(name = "doctorID") UUID doctorID,
            @RequestBody CreateDoctorRequest request) {
        return doctorService.updateDoctorByID(doctorID, request);
    }

    @DeleteMapping("/{doctorID}")
    public String deleteDoctorByID(
            @PathVariable(name = "doctorID") UUID doctorID) {
        return doctorService.deleteDoctorByID(doctorID);
    }

    @GetMapping("/{doctorID}/appointments")
    public List<AppointmentResponse> findDoctorAppointments(
            @PathVariable(name = "doctorID") UUID doctorID) {
        return appointmentService.findDoctorAppointments(doctorID);
    }
}