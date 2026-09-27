package com.project.medical_clinic_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class LoginResponse {
    private UUID id;
    private String name;

    public LoginResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
