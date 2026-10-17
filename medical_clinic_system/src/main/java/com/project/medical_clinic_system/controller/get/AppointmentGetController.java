package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.enums.AppointmentStatus;
import com.project.medical_clinic_system.exception.InvalidDataException;
import com.project.medical_clinic_system.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentGetController {

    private final AppointmentService appointmentService;

    public AppointmentGetController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/?status=}")
    public Page<AppointmentResponse> getAppointmentsByStatus(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "status") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        if (size > 10 || size <= 0) {
            throw new InvalidDataException("Max size");
        }
        if (page < 0){
            throw new InvalidDataException("Invalid page");
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        return appointmentService.findAppointmentsByStatus(status, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/?start=&end=")
    public Page<AppointmentResponse> getAppointmentsBySDate(
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "status") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        if (size > 10 || size <= 0) {
            throw new InvalidDataException("Max size");
        }
        if (page < 0){
            throw new InvalidDataException("Invalid page");
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        return appointmentService.findAppointmentsByDate(start,end, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public AppointmentResponse findAppointmentByID(
            @PathVariable(name = "id") UUID id) {

        return appointmentService.findAppointmentByID(id);
    }
}