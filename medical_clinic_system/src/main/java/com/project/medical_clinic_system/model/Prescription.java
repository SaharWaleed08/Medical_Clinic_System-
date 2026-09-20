package com.project.medical_clinic_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "record_id", nullable = false)
    private UUID recordID;
    @Column(name = "medication_name", nullable = false)
    private String medicationName;
    @Column(name = "dosage", nullable = false)
    private Integer dosage;
    @Column(name = "frequency", nullable = false)
    private Integer frequency;
    @Column(name = "treatment_duration", nullable = false)
    private Integer treatmentDuration;
    @Column(name = "additional_instruction", nullable = false)
    private String additionalInstruction;

    public Prescription() {
    }

    public Prescription(UUID recordID, String medicationName, Integer dosage, Integer frequency, Integer treatmentDuration, String additionalInstruction) {
        this.recordID = recordID;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.treatmentDuration = treatmentDuration;
        this.additionalInstruction = additionalInstruction;
    }


}
