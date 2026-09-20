package com.project.medical_clinic_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AvailableSlotResponse {

    private LocalDateTime startTime;

    public AvailableSlotResponse(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}