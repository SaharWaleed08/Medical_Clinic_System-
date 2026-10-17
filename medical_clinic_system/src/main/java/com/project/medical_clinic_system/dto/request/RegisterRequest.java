package com.project.medical_clinic_system.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.enums.Role;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public record RegisterRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 5, max = 100)
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "invalid password"
        )
        String password,

        @NotBlank(message = "Phone is required")
        @Pattern(
                regexp = "^01[0125][0-9]{8}$",
                message = "Invalid phone number"
        )
        String phone,

        @NotNull(message = "Role is required")
        Role role,

        // Patient fields
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth,

        Gender gender,

        LocalDate registrationDate,

        // Doctor fields
        String licenseNumber,

        @Min(value = 0, message = "Years of experience cannot be negative")
        Integer yearsOfExperience,

        @Positive(message = "Consultation fee must be greater than 0")
        BigDecimal consultationFee,

        @NotNull
        UUID specializationID,

        //Admin fields
        String adminPassword

) {
}