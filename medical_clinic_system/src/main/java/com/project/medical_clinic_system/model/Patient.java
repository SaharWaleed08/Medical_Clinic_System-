package com.project.medical_clinic_system.model;

import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.enums.Role;
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

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    public Patient() {
    }

    public Patient(String name, String email, String password, String phone,
                   LocalDate dateOfBirth, Gender gender, LocalDateTime registrationDate) {
        super(name, email, password, phone, Role.PATIENT);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.registrationDate = registrationDate;
    }
}