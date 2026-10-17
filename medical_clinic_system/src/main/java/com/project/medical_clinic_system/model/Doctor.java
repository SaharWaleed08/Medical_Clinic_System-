package com.project.medical_clinic_system.model;

import com.project.medical_clinic_system.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "doctors")
public class Doctor extends User {

    @ManyToOne(optional = false)
    @JoinColumn(name = "specialization_id",nullable = false)
    private Specialization specialization;

    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    @Column(name = "years_of_experience", nullable = false)
    private Integer yearsOfExperience;

    @Column(name = "consultation_fee")
    private BigDecimal consultationFee;

    @OneToMany(mappedBy = "doctor")
    private List<Availability> availabilities;

    public Doctor() {

    }

    public Doctor(String name, String email, String password, String phone,Specialization specialization,
                  String licenseNumber, Integer yearsOfExperience,
                  BigDecimal consultationFee) {

        super(name, email, password, phone, Role.DOCTOR);

        this.licenseNumber = licenseNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.consultationFee = consultationFee;
        this.specialization=specialization;
    }
}