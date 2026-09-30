package com.project.medical_clinic_system.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PatientResponse {
    private UUID patientID;
    private String name;
    private String email;
    private LocalDateTime registrationDate;

    public PatientResponse(UUID patientID, String name, String email, LocalDateTime registrationDate) {
        this.patientID = patientID;
        this.name = name;
        this.email = email;
        this.registrationDate = registrationDate;
    }

}
