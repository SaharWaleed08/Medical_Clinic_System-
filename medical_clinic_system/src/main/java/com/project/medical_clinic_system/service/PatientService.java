package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreatePatientRequest;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.mapper.PatientMapper;
import com.project.medical_clinic_system.repository.PatientRepository;
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
        return new PatientResponse(patient.getPatientID(), patient.getName(), patient.getEmail(), patient.getRegistrationDate());
    }

    public PatientResponse findPatientByID(UUID patientID) {
        Optional<Patient> patient = patientRepository.findById(patientID);
        return patientMapper.toResponse(patient);
    }

    public List<PatientResponse> findAllPatient() {
        List<Patient> patients = patientRepository.findAll();
        return patientMapper.toResponse(patients);
    }

    public PatientResponse updatePatientByID(UUID patientID, CreatePatientRequest request) {

        boolean existingPatient = patientRepository.existsById(patientID);

        if (existingPatient) {

            Optional<Patient> patient = patientRepository.findById(patientID);

            if (patient.isPresent()) {

                Patient existingPatientData = patient.get();

                existingPatientData.setName(request.getName());
                existingPatientData.setEmail(request.getEmail());
                existingPatientData.setPhone(request.getPhone());

                Optional<Patient> updatedPatient = patientRepository.save(existingPatientData);

                return patientMapper.toResponse(updatedPatient);
            }
        }

        throw new RuntimeException("Patient not found");
    }

    public String deletePatientByID(UUID patientID) {
        patientRepository.deleteById(patientID);
        return "Patient is deleted";
    }
}
