package com.project.medical_clinic_system.model;

import jakarta.persistence.*;
import com.project.medical_clinic_system.dto.request.CreatePatientRequest;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue
    private UUID doctorID;
    @Column(name = "doctor_name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "phone", nullable = false)
    private String phone;
    @Column(name = "license_number",nullable = false)
    private String licenseNumber;
    @Column(name = "years_of_experience",nullable = false)
    private Integer yearsOfExperience;
    @Column(name = "consultation_fee")
    private BigDecimal consultationFee;

    public Doctor() {

    }

    public Doctor(String name, String email, String password, String phone, String licenseNumber, Integer yearsOfExperience, BigDecimal consultationFee) {
        this.doctorID = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.consultationFee = consultationFee;
    }

    public UUID getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(UUID doctorID) {
        this.doctorID = doctorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }
}
