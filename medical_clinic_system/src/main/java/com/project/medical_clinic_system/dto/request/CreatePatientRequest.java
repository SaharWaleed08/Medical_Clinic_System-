package com.project.medical_clinic_system.dto.request;

import com.project.medical_clinic_system.model.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreatePatientRequest {
    private String name;
    private String password;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Gender gender;
    private LocalDateTime registrationDate;

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
