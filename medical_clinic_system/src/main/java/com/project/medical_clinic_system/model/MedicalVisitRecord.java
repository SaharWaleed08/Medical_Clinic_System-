package com.project.medical_clinic_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.util.UUID;

@Table(name = "medical_visit_records")
@Entity
@Getter
@Setter
public class MedicalVisitRecord {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "patient_id", nullable = false)
    private UUID patientID;
    @Column(name = "doctor_id", nullable = false)
    private UUID doctorID;
    @Column(name = "appointment_id", nullable = false, unique = true)
    private UUID appointmentID;
    @Column(name = "diagnosis", nullable = false)
    private String diagnosis;
    @Column(name = "medical_notes", nullable = false)
    private String medicalNotes;
    @Column(name = "record_creation_date", nullable = false)
    private LocalDate recordCreationDate;

    public MedicalVisitRecord() {
    }

    public MedicalVisitRecord(UUID patientID, UUID doctorID, UUID appointmentID, String diagnosis, String medicalNotes, LocalDate recordCreationDate) {
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.appointmentID = appointmentID;
        this.diagnosis = diagnosis;
        this.medicalNotes = medicalNotes;
        this.recordCreationDate = recordCreationDate;
    }
}

