package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateAppointmentRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public AppointmentResponse createAppointment(
            @RequestBody CreateAppointmentRequest request) {

        return appointmentService.createAppointment(request);
    }

    @GetMapping("/{id}")
    public AppointmentResponse findAppointmentByID(
            @PathVariable UUID id) {

        return appointmentService.findAppointmentByID(id);
    }
}