package com.project.medical_clinic_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePrescriptionRequest {
    @NotBlank(message = "Record id is required")
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
            message = "Invalid id"
    )
    private UUID recordID;
    @NotBlank
    private String medicationName;
    @NotBlank
    @Positive
    private Integer dosage;
    @NotBlank
    @Positive
    private Integer frequency;
    @NotBlank
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
