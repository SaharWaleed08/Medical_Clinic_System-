package com.project.medical_clinic_system.controller;

import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.service.DoctorService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }
    @PostMapping
    public DoctorResponse createDoctor(@RequestBody CreateDoctorRequest request) {
        return doctorService.createDoctor(request);
    }

    @GetMapping("/{patientID}")
    public DoctorResponse getDoctorByID(@PathVariable UUID doctorID) {
        return doctorService.findDoctorByID(doctorID);
    }

    @GetMapping
    public List<DoctorResponse> findAllDoctor() {
        return doctorService.findAllDoctor();
    }

    @PutMapping("/{patientID}")
    public DoctorResponse updateDoctorByID(@PathVariable UUID doctorID, @RequestBody CreateDoctorRequest request) {
        return doctorService.updateDoctorByID(doctorID,request);
    }
    @DeleteMapping("/{patientID}")
    public String deleteDoctorByID(@PathVariable UUID doctorID){
        return doctorService.deleteDoctorByID(doctorID);
    }

}
