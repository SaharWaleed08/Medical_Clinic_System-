package com.project.medical_clinic_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DoctorResponse {

    private UUID id;
    private String name;
    private String email;
    private String specialization;

    public DoctorResponse(UUID id, String name, String email, String specialization) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.specialization = specialization;
    }
}