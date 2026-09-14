package com.project.medical_clinic_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "patients")
public class Patient extends User {

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    public Patient() {

    }

    public Patient(String name, String email, String password, String phone,
                   LocalDate dateOfBirth, Gender gender,
                   LocalDateTime registrationDate) {

        super(name, email, password, phone);

        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.registrationDate = registrationDate;
    }
}