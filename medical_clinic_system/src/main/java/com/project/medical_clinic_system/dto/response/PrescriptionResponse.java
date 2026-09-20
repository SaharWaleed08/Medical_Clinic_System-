package com.project.medical_clinic_system.dto.response;

import java.util.UUID;

public class PrescriptionResponse {
    private UUID prescriptionID;
    private UUID recordID;
    private String medicationName;
    private Integer dosage;
    private Integer frequency;
    private Integer treatmentDuration;
    private String additionalInstruction;

    public PrescriptionResponse(UUID prescriptionID, UUID recordID, String medicationName, Integer dosage, Integer frequency, Integer treatmentDuration, String additionalInstruction) {
        this.prescriptionID = prescriptionID;
        this.recordID = recordID;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.treatmentDuration = treatmentDuration;
        this.additionalInstruction = additionalInstruction;
    }

    public UUID getPrescriptionID() {
        return prescriptionID;
    }

    public void setPrescriptionID(UUID prescriptionID) {
        this.prescriptionID = prescriptionID;
    }

    public UUID getRecordID() {
        return recordID;
    }

    public void setRecordID(UUID recordID) {
        this.recordID = recordID;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public Integer getDosage() {
        return dosage;
    }

    public void setDosage(Integer dosage) {
        this.dosage = dosage;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getTreatmentDuration() {
        return treatmentDuration;
    }

    public void setTreatmentDuration(Integer treatmentDuration) {
        this.treatmentDuration = treatmentDuration;
    }

    public String getAdditionalInstruction() {
        return additionalInstruction;
    }

    public void setAdditionalInstruction(String additionalInstruction) {
        this.additionalInstruction = additionalInstruction;
    }
}
