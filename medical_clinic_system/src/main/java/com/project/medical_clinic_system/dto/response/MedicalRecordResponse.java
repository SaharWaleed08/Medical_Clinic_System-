package com.project.medical_clinic_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
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

}
