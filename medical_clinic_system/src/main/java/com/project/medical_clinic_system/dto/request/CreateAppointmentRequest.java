package com.project.medical_clinic_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateAppointmentRequest {
    @NotBlank(message = "Patient id is required")
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
            message = "Invalid id"
    )
    private UUID patientID;
    @NotBlank(message = "Doctor id is required")
    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
            message = "Invalid id"
    )
    private UUID doctorID;
    @NotBlank
    private LocalDateTime appointmentDateTime;
    @NotBlank
    private String reasonForVisit;

    public CreateAppointmentRequest() {
    }

}