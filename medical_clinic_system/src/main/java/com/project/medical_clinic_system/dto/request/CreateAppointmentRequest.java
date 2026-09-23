package com.project.medical_clinic_system.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateAppointmentRequest {
    @NotBlank
    private UUID patientID;
    @NotBlank
    private UUID doctorID;
    @NotBlank
    private LocalDateTime appointmentDateTime;
    @NotBlank
    private String reasonForVisit;

    public CreateAppointmentRequest() {
    }

    public UUID getPatientID() {
        return patientID;
    }

    public void setPatientID(UUID patientID) {
        this.patientID = patientID;
    }

    public UUID getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(UUID doctorID) {
        this.doctorID = doctorID;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }
}