package com.project.medical_clinic_system.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class CreatePrescriptionRequest {
    @NotBlank
    private UUID recordID;
    @NotBlank
    private String medicationName;
    @NotBlank
    private Integer dosage;
    @NotBlank
    private Integer frequency;
    @NotBlank
    private Integer treatmentDuration;
    @NotBlank
    private String additionalInstruction;

    public CreatePrescriptionRequest(UUID recordID, String medicationName, Integer dosage, Integer frequency, Integer treatmentDuration, String additionalInstruction) {
        this.recordID = recordID;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.treatmentDuration = treatmentDuration;
        this.additionalInstruction = additionalInstruction;
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
