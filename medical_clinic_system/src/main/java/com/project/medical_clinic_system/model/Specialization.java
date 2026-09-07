package com.project.medical_clinic_system.model;

import jakarta.persistence.*;

import java.util.UUID;
@Entity
@Table(name = "specializations")
public class Specialization {
    @Id
    @GeneratedValue
    private UUID specializationID;
    @Column(name = "specialization_name",nullable = false,unique = true)
    private String name;
    @Column(name = "description",nullable = false)
    private String description;

    public Specialization(){

    }

    public Specialization( String name, String description) {
        this.specializationID = UUID.randomUUID();
        this.name = name;
        this.description = description;
    }

    public UUID getSpecializationID() {
        return specializationID;
    }

    public void setSpecializationID(UUID specializationID) {
        this.specializationID = specializationID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
