package com.project.medical_clinic_system.model;

import jakarta.persistence.*;


import java.time.LocalDate;
import java.util.UUID;

@Table(name = "medical_visit_records")
@Entity
public class MedicalVisitRecord {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "patient_id",nullable = false,unique = true)
    private UUID patientID;
    @Column(name = "doctor_id",nullable = false,unique = true)
    private UUID doctorID;
    @Column(name = "appointment_id",nullable = false,unique = true)
    private UUID appointmentID;
    @Column(name = "diagnosis",nullable = false)
    private String diagnosis;
    @Column(name = "medical_notes",nullable = false)
    private String medicalNotes;
    @Column(name = "record_creation_date",nullable = false)
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPatientID() {
        return patientID;
    }

    public void setPatientID(UUID patientID) {
        this.patientID = patientID;
    }

    public UUID getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(UUID doctorID) {
        this.doctorID = doctorID;
    }

    public UUID getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(UUID appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
    }

    public LocalDate getRecordCreationDate() {
        return recordCreationDate;
    }

    public void setRecordCreationDate(LocalDate recordCreationDate) {
        this.recordCreationDate = recordCreationDate;
    }
}

