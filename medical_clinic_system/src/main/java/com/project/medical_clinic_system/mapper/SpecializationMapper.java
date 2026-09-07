package com.project.medical_clinic_system.mapper;

import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.dto.response.SpecializationResponse;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.model.Specialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpecializationMapper {
    public SpecializationResponse toResponse(Optional<Specialization> specialization) {
        return new SpecializationResponse(specialization.get().getSpecializationID(),specialization.get().getName(),specialization.get().getDescription());
    }

    public List<SpecializationResponse> toResponse(List<Specialization> specializations) {
        List<SpecializationResponse> responses = new ArrayList<>();

        for (Specialization specialization : specializations) {
            responses.add(toResponse(Optional.ofNullable(specialization)));
        }

        return responses;
    }
}
