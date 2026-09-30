package com.project.medical_clinic_system.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;
import com.project.medical_clinic_system.enums.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(name = "reason_for_visit")
    private String reasonForVisit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    public Appointment() {
    }

    public Appointment(Patient patient, Doctor doctor,
                       LocalDateTime appointmentDateTime,
                       String reasonForVisit) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDateTime = appointmentDateTime;
        this.reasonForVisit = reasonForVisit;
        this.status = AppointmentStatus.PENDING;
    }
}