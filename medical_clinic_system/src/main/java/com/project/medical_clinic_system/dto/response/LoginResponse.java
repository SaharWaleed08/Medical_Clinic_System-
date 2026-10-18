package com.project.medical_clinic_system.dto.response;

import com.project.medical_clinic_system.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class LoginResponse {
    private UUID id;
    private String name;
    private Role role;
    private String token;

    public LoginResponse(UUID id, String name, Role role, String token) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.token = token;
    }
}
