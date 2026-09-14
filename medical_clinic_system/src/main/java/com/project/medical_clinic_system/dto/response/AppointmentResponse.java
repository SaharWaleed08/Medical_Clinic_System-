package com.project.medical_clinic_system.dto.response;

import com.project.medical_clinic_system.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentResponse {

    private UUID id;
    private UUID patientID;
    private String patientName;
    private UUID doctorID;
    private String doctorName;
    private LocalDateTime appointmentDateTime;
    private String reasonForVisit;
    private AppointmentStatus status;

    public AppointmentResponse() {
    }

    public AppointmentResponse(UUID id, UUID patientID, String patientName,
                               UUID doctorID, String doctorName,
                               LocalDateTime appointmentDateTime,
                               String reasonForVisit,
                               AppointmentStatus status) {
        this.id = id;
        this.patientID = patientID;
        this.patientName = patientName;
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.appointmentDateTime = appointmentDateTime;
        this.reasonForVisit = reasonForVisit;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPatientID() {
        return patientID;
    }

    public void setPatientID(UUID patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public UUID getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(UUID doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
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

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}