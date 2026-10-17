package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.AvailableSlotResponse;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.exception.InvalidDataException;
import com.project.medical_clinic_system.service.AppointmentService;
import com.project.medical_clinic_system.service.DoctorService;
import com.project.medical_clinic_system.service.SpecializationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorGetController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorGetController(DoctorService doctorService,
                               AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public Page<DoctorResponse> getDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (size > 10 || size <= 0) {
            throw new InvalidDataException("Max size");
        }
        if (page < 0) {
            throw new InvalidDataException("Invalid page");
        }
        if (sortBy.equalsIgnoreCase("password")) {
            throw new InvalidDataException("Invalid sort field");
        }

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        return doctorService.findDoctors(name, specialization, pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{doctorID}")
    public DoctorResponse getDoctorByID(
            @PathVariable(name = "doctorID") UUID doctorID) {
        return doctorService.findDoctorByID(doctorID);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_DOCTOR') and #doctorID == authentication.principal.id)")
    @GetMapping("/{doctorID}/appointments")
    public Page<AppointmentResponse> findDoctorAppointments(
            @PathVariable(name = "doctorID") UUID doctorID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        if (size > 10 || size <= 0) {
            throw new InvalidDataException("Max size");
        }
        if (page < 0){
            throw new InvalidDataException("Invalid page");
        }
            if (sortBy.equalsIgnoreCase("password")) {
                throw new InvalidDataException("Can't sort by password");
            }

        Pageable pageable = PageRequest.of(page, size, sort);
        return appointmentService.findDoctorAppointments(doctorID, pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/available-slots")
    public List<AvailableSlotResponse> getAvailableSlots(
            @PathVariable(name = "id") UUID id,
            @RequestParam LocalDate date) {

        return appointmentService.getAvailableSlots(id, date);
    }
}