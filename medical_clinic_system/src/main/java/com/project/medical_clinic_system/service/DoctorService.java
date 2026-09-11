package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.mapper.DoctorMapper;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.repository.DoctorRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }
    public DoctorResponse createDoctor(CreateDoctorRequest request) {
        Doctor doctor = new Doctor(request.getName(), request.getEmail(), request.getPassword(), request.getPhone(),request.getLicenseNumber(),request.getYearsOfExperience(),request.getConsultationFee());
        doctorRepository.save(doctor);
        return new DoctorResponse(doctor.getDoctorID(), doctor.getName(), doctor.getEmail());
    }

    public DoctorResponse findDoctorByID(UUID doctorID) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorID);
        return doctorMapper.toResponse(doctor);
    }

    public List<DoctorResponse> findAllDoctor() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctorMapper.toResponse(doctors);
    }

    public DoctorResponse updateDoctorByID(UUID doctorID, CreateDoctorRequest request) {
        Optional<Doctor> doctor=doctorRepository.updateById(doctorID,request);
        return doctorMapper.toResponse(doctor);
    }

    public String deleteDoctorByID(UUID doctorID) {
        doctorRepository.deleteById(doctorID);
        return "Doctor is deleted";
    }

}
