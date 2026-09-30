package com.project.medical_clinic_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SpecializationResponse {
    private UUID specializationID;
    private String name;
    private String description;

    public SpecializationResponse(UUID specializationID, String name, String description) {
        this.specializationID = specializationID;
        this.name = name;
        this.description = description;
    }

}
