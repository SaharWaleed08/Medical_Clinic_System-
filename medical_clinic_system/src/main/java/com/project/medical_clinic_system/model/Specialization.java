package com.project.medical_clinic_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Setter
@Getter
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
        this.name = name;
        this.description = description;
    }
}
