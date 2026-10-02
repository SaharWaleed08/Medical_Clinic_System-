package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.exception.DuplicateResourceException;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.DoctorMapper;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public DoctorResponse createDoctor(CreateDoctorRequest request) {

        if (!doctorRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email already exists");
        }
        Doctor doctor = new Doctor(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                request.getLicenseNumber(),
                request.getYearsOfExperience(),
                request.getConsultationFee()
        );

        doctorRepository.save(doctor);

        return doctorMapper.toResponse(doctor);
    }

    public Page<DoctorResponse> findDoctors(String name, Pageable pageable) {

        Page<Doctor> doctors;

        if (name == null || name.isBlank()) {
            doctors = doctorRepository.findAll(pageable);
        } else {
            doctors = doctorRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        return doctors.map(doctorMapper::toResponse);
    }

    public DoctorResponse findDoctorByID(UUID doctorID) {
        Doctor doctor = doctorRepository.findById(doctorID)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        return doctorMapper.toResponse(doctor);
    }

    public DoctorResponse updateDoctorByID(UUID doctorID, CreateDoctorRequest request) {

        Doctor doctor = doctorRepository.findById(doctorID)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));


        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPassword(passwordEncoder.encode(request.getPassword()));
        doctor.setPhone(request.getPhone());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setYearsOfExperience(request.getYearsOfExperience());
        doctor.setConsultationFee(request.getConsultationFee());

        doctorRepository.save(doctor);

        return doctorMapper.toResponse(doctor);
    }

    public String deleteDoctorByID(UUID doctorID) {

        doctorRepository.deleteById(doctorID);

        return "Doctor is deleted";
    }
}