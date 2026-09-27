package com.project.medical_clinic_system.dto.request;

import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.enums.Role;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;


public record RegisterRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotNull(message = "Role is required")
        Role role,

        // Patient fields
        LocalDate dateOfBirth,

        Gender gender,

        LocalDate registrationDate,

        // Doctor fields
        String licenseNumber,

        @Min(value = 0, message = "Years of experience cannot be negative")
        Integer yearsOfExperience,

        @Positive(message = "Consultation fee must be greater than 0")
        BigDecimal consultationFee,

        //Admin fields
        String adminPassword

) {
}