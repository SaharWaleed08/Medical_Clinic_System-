package com.project.medical_clinic_system.dto.request;

import java.time.LocalDate;
import java.util.UUID;

public class CreateMedicalRecordRequest {
    private UUID patientID;
    private UUID doctorID;
    private UUID appointmentID;
    private String diagnosis;
    private String medicalNotes;
    private LocalDate recordCreationDate;


    public CreateMedicalRecordRequest(UUID patientID, UUID doctorID, UUID appointmentID, String diagnosis, String medicalNotes, LocalDate recordCreationDate) {
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.appointmentID = appointmentID;
        this.diagnosis = diagnosis;
        this.medicalNotes = medicalNotes;
        this.recordCreationDate = recordCreationDate;
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
