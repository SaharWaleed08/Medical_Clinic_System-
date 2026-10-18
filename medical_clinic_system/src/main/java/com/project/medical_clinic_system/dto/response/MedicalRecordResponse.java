package com.project.medical_clinic_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MedicalRecordResponse {
    private UUID medicalRecordID;
    private UUID patientID;
    private UUID appointmentID;
    private String diagnosis;
    private String medicalNotes;

    public MedicalRecordResponse(UUID medicalRecordID, UUID patientID, UUID appointmentID, String diagnosis, String medicalNotes) {
        this.medicalRecordID = medicalRecordID;
        this.patientID = patientID;
        this.appointmentID = appointmentID;
        this.diagnosis = diagnosis;
        this.medicalNotes = medicalNotes;
    }
}
