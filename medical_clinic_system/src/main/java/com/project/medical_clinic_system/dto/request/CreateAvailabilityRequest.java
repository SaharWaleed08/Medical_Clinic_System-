package com.project.medical_clinic_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class CreateAvailabilityRequest {
    @NotBlank(message = "Doctor id is required")
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
            message = "Invalid id"
    )
    private UUID doctorID;
    @NotBlank
    private DayOfWeek day;
    @NotBlank
    private LocalTime startTime;
    @NotBlank
    private LocalTime endTime;


    public CreateAvailabilityRequest(UUID doctorID, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this.doctorID = doctorID;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}


