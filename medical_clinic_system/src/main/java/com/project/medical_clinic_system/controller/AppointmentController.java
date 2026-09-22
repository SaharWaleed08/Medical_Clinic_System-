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

    @PatchMapping("/{id}/confirm")
    public AppointmentResponse confirmAppointment(
            @PathVariable(name = "id") UUID id) {

        return appointmentService.confirmAppointment(id);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancelAppointment(
            @PathVariable(name = "id") UUID id) {

        return appointmentService.cancelAppointment(id);
    }

    @PatchMapping("/{id}/complete")
    public AppointmentResponse completeAppointment(
            @PathVariable(name = "id") UUID id) {

        return appointmentService.completeAppointment(id);
    }

    @PatchMapping("/{id}/no-show")
    public AppointmentResponse markAppointmentAsNoShow(
            @PathVariable(name = "id") UUID id) {

        return appointmentService.markAppointmentAsNoShow(id);
    }
}