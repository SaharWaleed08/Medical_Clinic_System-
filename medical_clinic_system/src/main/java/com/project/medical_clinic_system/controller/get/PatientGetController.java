package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.exception.InvalidDataException;
import com.project.medical_clinic_system.service.AppointmentService;
import com.project.medical_clinic_system.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientGetController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientGetController(PatientService patientService,
                                AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or (isAuthenticated() and #patientID == authentication.principal.id)")
    @GetMapping("/{patientID}")
    public PatientResponse getPatientByID(
            @PathVariable(name = "patientID") UUID patientID) {
        return patientService.findPatientByID(patientID);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR')")
    @GetMapping
    public Page<PatientResponse> getPatients(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        if (size > 10 || size <= 0) {
            throw new InvalidDataException("Max size");
        }
        if (page < 0) {
            throw new InvalidDataException("Invalid page");
        }
        if (sortBy.equalsIgnoreCase("password")) {
            throw new InvalidDataException("Can't sort by password");
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        return patientService.findPatients(name, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or (isAuthenticated() and #patientID == authentication.principal.id)")
    @GetMapping("/{patientID}/appointments")
    public Page<AppointmentResponse> findPatientAppointments(
            @PathVariable(name = "patientID") UUID patientID,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        if (size > 10 || size <= 0) {
            throw new InvalidDataException("Max size");
        }
        if (page < 0) {
            throw new InvalidDataException("Invalid page");
        }
        if (sortBy.equalsIgnoreCase("password")) {
            throw new InvalidDataException("Can't sort by password");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return appointmentService.findPatientAppointments(patientID, pageable);
    }
}