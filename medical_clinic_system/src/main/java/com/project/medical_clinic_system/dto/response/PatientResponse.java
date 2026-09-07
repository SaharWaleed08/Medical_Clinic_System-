package com.project.medical_clinic_system.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class PatientResponse {
    private UUID patientID;
    private String name;
    private String email;
    private LocalDateTime registrationDate;

    public PatientResponse(UUID patientID, String name, String email, LocalDateTime registrationDate) {
        this.patientID = patientID;
        this.name = name;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    public UUID getPatientID() {
        return patientID;
    }

    public void setPatientID(UUID patientID) {
        this.patientID = patientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
