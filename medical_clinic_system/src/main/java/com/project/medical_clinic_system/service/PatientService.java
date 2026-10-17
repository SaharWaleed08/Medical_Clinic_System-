package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreatePatientRequest;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.exception.DuplicateResourceException;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.mapper.PatientMapper;
import com.project.medical_clinic_system.repository.PatientRepository;
import com.project.medical_clinic_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public PatientResponse createPatient(CreatePatientRequest request) {
        if (userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email already exists");
        }
        Patient patient = new Patient(request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getPhone(), request.getDateOfBirth(), request.getGender(), request.getRegistrationDate());
        patientRepository.save(patient);
        return patientMapper.toResponse(patient);
    }

    public Page<PatientResponse> findPatients(String name, Pageable pageable) {

        Page<Patient> patients;

        if (name == null || name.isBlank()) {
            patients = patientRepository.findAll(pageable);
        } else {
            patients = patientRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        return patients.map(patient -> new PatientResponse(patient.getId(), patient.getName(), patient.getEmail(), patient.getRegistrationDate())
        );
    }

    public PatientResponse findPatientByID(UUID patientID) {
        Patient patient = patientRepository.findById(patientID)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return patientMapper.toResponse(patient);
    }

    public PatientResponse updatePatientByID(UUID patientID, CreatePatientRequest request) {

        Patient patient = patientRepository.findById(patientID)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));


        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setPassword(passwordEncoder.encode(request.getPassword()));
        patient.setPhone(request.getPhone());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setRegistrationDate(request.getRegistrationDate());


        return patientMapper.toResponse(patient);
    }

    public String deletePatientByID(UUID patientID) {
        patientRepository.deleteById(patientID);
        return "Patient is deleted";
    }
}
