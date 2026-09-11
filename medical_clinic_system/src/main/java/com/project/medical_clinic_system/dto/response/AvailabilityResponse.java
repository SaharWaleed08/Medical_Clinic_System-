package com.project.medical_clinic_system.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public class AvailabilityResponse {
    private UUID doctorID;
    private String name;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;

    public AvailabilityResponse(UUID doctorID, String name, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this.doctorID = doctorID;
        this.name = name;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(UUID doctorID) {
        this.doctorID = doctorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
