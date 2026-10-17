package com.project.medical_clinic_system.dto.request;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateDoctorRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 5, max = 100)
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "invalid email")
    private String email;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "invalid password"
    )
    private String password;
    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^01[0125][0-9]{8}$",
            message = "Invalid phone number"
    )
    private String phone;
    @NotBlank(message = "License number is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{4}$",
            message = "License number must be in format 1234-5678"
    )
    private String licenseNumber;
    @NotNull(message = "Years of experience is required")
    @Positive
    private Integer yearsOfExperience;
    @NotNull
    @Positive
    private BigDecimal consultationFee;
    @NotNull
    private UUID specializationID;


    public CreateDoctorRequest(String name, String email, String password, String phone, String licenseNumber, Integer yearsOfExperience, BigDecimal consultationFee,UUID specializationID) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.consultationFee = consultationFee;
        this.specializationID=specializationID;
    }

}
