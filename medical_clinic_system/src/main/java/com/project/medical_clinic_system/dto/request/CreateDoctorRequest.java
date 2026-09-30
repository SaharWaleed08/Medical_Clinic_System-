package com.project.medical_clinic_system.dto.request;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateDoctorRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 10, max = 100)
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
    @NotBlank(message = "Years of experience is required")
    @Positive
    private Integer yearsOfExperience;
    @NotBlank
    @Positive
    private BigDecimal consultationFee;


    public CreateDoctorRequest(String name, String email, String password, String phone, String licenseNumber, Integer yearsOfExperience, BigDecimal consultationFee) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.consultationFee = consultationFee;
    }

}
