package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateAppointmentRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasRole('ROLE_PATIENT') and #request.patientID == authentication.principal.id")
    @PostMapping
    public AppointmentResponse createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request) {
        return appointmentService.createAppointment(request);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DOCTOR') and " +
            "@appointmentRepository.findById(#id).orElse(null)?.doctor?.id == authentication.principal.id)")
    @PatchMapping("/{id}/confirm")
    public AppointmentResponse confirmAppointment(
            @PathVariable(name = "id") UUID id) {

        return appointmentService.confirmAppointment(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or" +
            " (hasRole('ROLE_DOCTOR') and @appointmentRepository.findById(#id).orElse(null)?.doctor?.id == authentication.principal.id) or" +
            "(hasRole('ROLE_PATIENT') and @appointmentRepository.findById(#id).orElse(null)?.patient?.id == authentication.principal.id)")
    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancelAppointment(
            @PathVariable(name = "id") UUID id) {

        return appointmentService.cancelAppointment(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DOCTOR') and " +
            "@appointmentRepository.findById(#id).orElse(null)?.doctor?.id == authentication.principal.id)")
    @PatchMapping("/{id}/complete")
    public AppointmentResponse completeAppointment(
            @PathVariable(name = "id") UUID id) {

        return appointmentService.completeAppointment(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DOCTOR') and " +
            "@appointmentRepository.findById(#id).orElse(null)?.doctor?.id == authentication.principal.id)")
    @PatchMapping("/{id}/no-show")
    public AppointmentResponse markAppointmentAsNoShow(
            @PathVariable(name = "id") UUID id) {

        return appointmentService.markAppointmentAsNoShow(id);
    }
}