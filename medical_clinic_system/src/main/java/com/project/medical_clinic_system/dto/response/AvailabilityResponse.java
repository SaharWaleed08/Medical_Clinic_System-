package com.project.medical_clinic_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class AvailabilityResponse {

    private UUID availabilityID;
    private UUID doctorID;
    private String name;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;

    public AvailabilityResponse(UUID availabilityID, UUID doctorID, String name,
                                DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this.availabilityID = availabilityID;
        this.doctorID = doctorID;
        this.name = name;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}