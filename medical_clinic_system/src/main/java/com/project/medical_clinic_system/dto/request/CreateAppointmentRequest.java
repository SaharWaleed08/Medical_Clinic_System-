package com.project.medical_clinic_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateAppointmentRequest {
    @NotNull(message = "Patient id is required")
    private UUID patientID;
    @NotNull(message = "Doctor id is required")
    private UUID doctorID;
    @NotNull
    private LocalDateTime appointmentDateTime;
    @NotBlank
    private String reasonForVisit;

    public CreateAppointmentRequest() {
    }

}