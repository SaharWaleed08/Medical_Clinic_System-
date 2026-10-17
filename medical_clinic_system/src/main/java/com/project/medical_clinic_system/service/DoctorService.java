package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.exception.DuplicateResourceException;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.DoctorMapper;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Specialization;
import com.project.medical_clinic_system.repository.DoctorRepository;
import com.project.medical_clinic_system.repository.SpecializationRepository;
import com.project.medical_clinic_system.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper, PasswordEncoder passwordEncoder, UserRepository userRepository, SpecializationRepository specializationRepository) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.specializationRepository = specializationRepository;
    }

    public DoctorResponse createDoctor(CreateDoctorRequest request) {

        Specialization specialization = specializationRepository.findById(request.getSpecializationID())
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        Doctor doctor = new Doctor(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                specialization,
                request.getLicenseNumber(),
                request.getYearsOfExperience(),
                request.getConsultationFee()
        );

        doctorRepository.save(doctor);

        return doctorMapper.toResponse(doctor);
    }

    public Page<DoctorResponse> findDoctors(String name, String specialization, Pageable pageable) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasSpecialization = specialization != null && !specialization.isBlank();

        Page<Doctor> doctors;
        if (hasName && hasSpecialization) {
            doctors = doctorRepository
                    .findByNameContainingIgnoreCaseAndSpecializationNameContainingIgnoreCase(
                            name, specialization, pageable);
        } else if (hasName) {
            doctors = doctorRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (hasSpecialization) {
            doctors = doctorRepository.findBySpecializationNameContainingIgnoreCase(specialization, pageable);
        } else {
            doctors = doctorRepository.findAll(pageable);
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