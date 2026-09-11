package com.project.medical_clinic_system.mapper;

import com.project.medical_clinic_system.dto.response.AvailabilityResponse;
import com.project.medical_clinic_system.model.Availability;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AvailabilityMapper {
    public AvailabilityResponse toResponse(Optional<Availability> availability) {
        return new AvailabilityResponse(availability.get().getDoctorID(), availability.get().getName(), availability.get().getDay(), availability.get().getStartTime(), availability.get().getEndTime());
    }

    public List<AvailabilityResponse> toResponse(List<Availability> availabilities) {
        List<AvailabilityResponse> responses = new ArrayList<>();
        for (Availability availability : availabilities){
            responses.add(toResponse(Optional.ofNullable(availability)));
        }
        return responses;
    }
}
