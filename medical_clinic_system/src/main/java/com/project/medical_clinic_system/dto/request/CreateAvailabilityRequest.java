package com.project.medical_clinic_system.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class CreateAvailabilityRequest {
    @NotNull(message = "Doctor id is required")
    private UUID doctorID;
    @NotNull
    private DayOfWeek day;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;


    public CreateAvailabilityRequest(UUID doctorID, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this.doctorID = doctorID;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}


