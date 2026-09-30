package com.project.medical_clinic_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
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

}
