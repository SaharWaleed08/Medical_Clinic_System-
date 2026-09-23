package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateAppointmentRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.enums.AppointmentStatus;
import com.project.medical_clinic_system.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<AppointmentResponse> getAppointments(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        return appointmentService.findAppointments(name, pageable);
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