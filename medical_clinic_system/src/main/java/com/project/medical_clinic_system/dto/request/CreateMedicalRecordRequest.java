package com.project.medical_clinic_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateMedicalRecordRequest {
    @NotNull(message = "Appointment id is required")
    private UUID appointmentID;
    @NotBlank
    private String diagnosis;
    @NotBlank
    private String medicalNotes;
    @NotNull
    private LocalDate recordCreationDate;


    public CreateMedicalRecordRequest(UUID appointmentID, String diagnosis, String medicalNotes, LocalDate recordCreationDate) {
        this.appointmentID = appointmentID;
        this.diagnosis = diagnosis;
        this.medicalNotes = medicalNotes;
        this.recordCreationDate = recordCreationDate;
    }

}
