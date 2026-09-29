package com.project.medical_clinic_system.mapper;

import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.model.Patient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PatientMapper {
    public PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getRegistrationDate());
    }

    public List<PatientResponse> toResponse(List<Patient> patients) {
        List<PatientResponse> responses = new ArrayList<>();

        for (Patient patient : patients) {
            responses.add(toResponse(patient));
        }

        return responses;
    }
}

