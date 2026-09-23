package com.project.medical_clinic_system.controller.get;

import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.AvailableSlotResponse;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.service.AppointmentService;
import com.project.medical_clinic_system.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping
    public Page<DoctorResponse> getDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        return doctorService.findDoctors(name, pageable);
    }

    @GetMapping("/{doctorID}")
    public DoctorResponse getDoctorByID(
            @PathVariable(name = "doctorID") UUID doctorID) {
        return doctorService.findDoctorByID(doctorID);
    }

    @GetMapping("/{doctorID}/appointments")
    public List<AppointmentResponse> findDoctorAppointments(
            @PathVariable(name = "doctorID") UUID doctorID) {
        return appointmentService.findDoctorAppointments(doctorID);
    }

    @GetMapping("/{id}/available-slots")
    public List<AvailableSlotResponse> getAvailableSlots(
            @PathVariable(name = "id") UUID id,
            @RequestParam LocalDate date) {

        return appointmentService.getAvailableSlots(id, date);
    }
}