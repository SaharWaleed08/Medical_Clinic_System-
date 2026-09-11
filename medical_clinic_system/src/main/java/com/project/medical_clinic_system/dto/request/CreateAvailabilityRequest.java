package com.project.medical_clinic_system.dto.request;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public class CreateAvailabilityRequest {
    private UUID doctorID;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;

    public CreateAvailabilityRequest(UUID doctorID, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this.doctorID = doctorID;
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


