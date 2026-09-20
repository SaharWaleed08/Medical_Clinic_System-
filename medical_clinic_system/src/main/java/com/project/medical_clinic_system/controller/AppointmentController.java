package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateAppointmentRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.enums.AppointmentStatus;
import com.project.medical_clinic_system.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
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

    @GetMapping
    public List<AppointmentResponse> findAllAppointments(
            @RequestParam(name = "status", required = false) AppointmentStatus status,
            @RequestParam(name = "start", required = false) LocalDateTime start,
            @RequestParam(name = "end", required = false) LocalDateTime end) {

        if (status != null) {
            return appointmentService.findAppointmentsByStatus(status);
        }

        if (start != null && end != null) {
            return appointmentService.findAppointmentsByDate(start, end);
        }

        return appointmentService.findAllAppointments();
    }

    @GetMapping("/{id}")
    public AppointmentResponse findAppointmentByID(
            @PathVariable(name = "id") UUID id) {

        return appointmentService.findAppointmentByID(id);
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