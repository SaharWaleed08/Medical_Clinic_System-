package com.project.medical_clinic_system.mapper;

import com.project.medical_clinic_system.dto.response.AvailabilityResponse;
import com.project.medical_clinic_system.model.Availability;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AvailabilityMapper {

    public AvailabilityResponse toResponse(Availability availability) {
        return new AvailabilityResponse(
                availability.getId(),
                availability.getDoctor().getId(),
                availability.getDoctor().getName(),
                availability.getDay(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }

    public List<AvailabilityResponse> toResponse(List<Availability> availabilities) {
        List<AvailabilityResponse> responses = new ArrayList<>();

        for (Availability availability : availabilities) {
            responses.add(toResponse(availability));
        }

        return responses;
    }
}