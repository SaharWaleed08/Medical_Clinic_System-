package com.project.medical_clinic_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSpecializationRequest {
    @NotBlank
    @Size(max = 100)
    private String name;
    private String description;

    public CreateSpecializationRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
