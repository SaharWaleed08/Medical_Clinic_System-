package com.project.medical_clinic_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePrescriptionRequest {
    @NotNull(message = "Record id is required")
    private UUID recordID;
    @NotBlank
    private String medicationName;
    @NotNull
    @Positive
    private Integer dosage;
    @NotNull
    @Positive
    private Integer frequency;
    @NotNull
    @Positive
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
}
