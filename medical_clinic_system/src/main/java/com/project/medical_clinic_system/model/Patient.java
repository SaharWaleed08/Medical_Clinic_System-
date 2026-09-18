package com.project.medical_clinic_system.model;

import com.project.medical_clinic_system.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "patients")
public class Patient extends User {

    @Id
    @GeneratedValue
    private UUID patientID;

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
        super(name, email, password, phone);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.registrationDate = registrationDate;
    }
}