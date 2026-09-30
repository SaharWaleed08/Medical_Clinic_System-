package com.project.medical_clinic_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAdminRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 10, max = 100)
    private String name;
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "invalid password"
    )
    private String password;
    @NotBlank(message = "Email is required")
    @Email(message = "invalid email")
    private String email;
    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^01[0125][0-9]{8}$",
            message = "Invalid phone number"
    )
    private String phone;
    private String adminPassword;

    public CreateAdminRequest(String name, String password, String email, String phone, String adminPassword) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.adminPassword = adminPassword;
    }
}
