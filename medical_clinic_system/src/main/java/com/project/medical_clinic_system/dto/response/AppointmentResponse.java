package com.project.medical_clinic_system.dto.response;

import com.project.medical_clinic_system.enums.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AppointmentResponse {

    private UUID id;
    private UUID patientID;
    private String patientName;
    private UUID doctorID;
    private String doctorName;
    private LocalDateTime appointmentDateTime;
    private String reasonForVisit;
    private AppointmentStatus status;


    public AppointmentResponse(UUID id, UUID patientID, String patientName,
                               UUID doctorID, String doctorName,
                               LocalDateTime appointmentDateTime,
                               String reasonForVisit,
                               AppointmentStatus status) {
        this.id = id;
        this.patientID = patientID;
        this.patientName = patientName;
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.appointmentDateTime = appointmentDateTime;
        this.reasonForVisit = reasonForVisit;
        this.status = status;
    }

}