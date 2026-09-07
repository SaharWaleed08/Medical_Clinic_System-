package com.project.medical_clinic_system.mapper;

import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientMapper {
    public PatientResponse toResponse(Optional<Patient> patient) {
        return new PatientResponse(patient.getPatientID(), patient.getName(), patient.getEmail(), patient.getRegistrationDate());
    }

    public List<PatientResponse> toResponse(List<Patient> patients) {
        List<PatientResponse> responses = new ArrayList<>();

        for (Patient patient : patients) {
            responses.add(toResponse(patient));
        }

        return responses;
    }
}

