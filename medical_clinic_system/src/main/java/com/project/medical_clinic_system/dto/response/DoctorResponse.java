package com.project.medical_clinic_system.dto.response;

import java.util.UUID;

public class DoctorResponse {
    private UUID doctorID;
    private String name;
    private String email;

    public DoctorResponse(UUID doctorID, String name, String email) {
        this.doctorID = doctorID;
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
