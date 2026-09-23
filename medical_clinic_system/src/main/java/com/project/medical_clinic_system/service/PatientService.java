package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreatePatientRequest;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.mapper.PatientMapper;
import com.project.medical_clinic_system.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    public PatientResponse createPatient(CreatePatientRequest request) {
        Patient patient = new Patient(request.getName(), request.getEmail(), request.getPassword(), request.getPhone(), request.getDateOfBirth(), request.getGender(), request.getRegistrationDate());
        patientRepository.save(patient);
        return new PatientResponse(patient.getId(), patient.getName(), patient.getEmail(), patient.getRegistrationDate());
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
        Optional<Patient> patient = patientRepository.findById(patientID);
        return patientMapper.toResponse(patient);
    }

    public PatientResponse updatePatientByID(UUID patientID, CreatePatientRequest request) {
        Optional<Patient> patient = patientRepository.findById(patientID);

        Patient existingPatient = patient.get();

        existingPatient.setName(request.getName());
        existingPatient.setEmail(request.getEmail());
        existingPatient.setPassword(request.getPassword());
        existingPatient.setPhone(request.getPhone());
        existingPatient.setDateOfBirth(request.getDateOfBirth());
        existingPatient.setGender(request.getGender());
        existingPatient.setRegistrationDate(request.getRegistrationDate());

        patientRepository.save(existingPatient);

        return patientMapper.toResponse(Optional.of(existingPatient));
    }

    public String deletePatientByID(UUID patientID) {
        patientRepository.deleteById(patientID);
        return "Patient is deleted";
    }
}
