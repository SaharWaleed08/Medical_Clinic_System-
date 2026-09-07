package com.project.medical_clinic_system.dto.response;

import jakarta.persistence.Column;

import java.util.UUID;

public class SpecializationResponse {
    private UUID specializationID;
    private String name;
    private String description;

    public SpecializationResponse(UUID specializationID, String name, String description) {
        this.specializationID = specializationID;
        this.name = name;
        this.description = description;
    }

    public UUID getSpecializationID() {
        return specializationID;
    }

    public void setSpecializationID(UUID specializationID) {
        this.specializationID = specializationID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
