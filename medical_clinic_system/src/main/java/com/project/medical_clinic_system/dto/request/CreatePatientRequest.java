package com.project.medical_clinic_system.dto.request;

import com.project.medical_clinic_system.enums.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreatePatientRequest {
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
    @NotBlank(message = "Date of birth is required")
    @Past
    private LocalDate dateOfBirth;
    @NotBlank(message = "Gender is required")
    private Gender gender;
    @NotBlank
    @PastOrPresent
    private LocalDateTime registrationDate;

    public CreatePatientRequest() {
    }

    public CreatePatientRequest(String name, String password, String email, String phone, LocalDate dateOfBirth, Gender gender, LocalDateTime registrationDate) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.registrationDate = registrationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}