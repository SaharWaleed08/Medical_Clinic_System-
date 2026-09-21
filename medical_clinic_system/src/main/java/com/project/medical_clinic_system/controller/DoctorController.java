package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.AvailableSlotResponse;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.service.AppointmentService;
import com.project.medical_clinic_system.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.SortedMap;
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

    @GetMapping("/{id}/available-slots")
    public List<AvailableSlotResponse> getAvailableSlots(
            @PathVariable(name = "id") UUID id,
            @RequestParam LocalDate date) {

        return appointmentService.getAvailableSlots(id, date);
    }
}