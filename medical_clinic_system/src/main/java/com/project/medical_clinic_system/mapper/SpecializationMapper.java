package com.project.medical_clinic_system.mapper;


import com.project.medical_clinic_system.dto.response.SpecializationResponse;
import com.project.medical_clinic_system.model.Specialization;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpecializationMapper {
    public SpecializationResponse toResponse(Specialization specialization) {
        return new SpecializationResponse(
                specialization.getSpecializationID(),
                specialization.getName(),
                specialization.getDescription());
    }

    public List<SpecializationResponse> toResponse(List<Specialization> specializations) {
        List<SpecializationResponse> responses = new ArrayList<>();

        for (Specialization specialization : specializations) {
            responses.add(toResponse(specialization));
        }

        return responses;
    }
}
