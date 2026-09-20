package com.project.medical_clinic_system.dto.response;

import java.util.UUID;

public class MedicalRecordResponse {
    private UUID medicalRecordID;
    private UUID patientID;
    private String diagnosis;
    private String medicalNotes;

    public MedicalRecordResponse(UUID medicalRecordID, UUID patientID, String diagnosis, String medicalNotes) {
        this.medicalRecordID = medicalRecordID;
        this.patientID = patientID;
        this.diagnosis = diagnosis;
        this.medicalNotes = medicalNotes;
    }

    public UUID getMedicalRecordID() {
        return medicalRecordID;
    }

    public void setMedicalRecordID(UUID medicalRecordID) {
        this.medicalRecordID = medicalRecordID;
    }

    public UUID getPatientID() {
        return patientID;
    }

    public void setPatientID(UUID patientID) {
        this.patientID = patientID;
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
}
