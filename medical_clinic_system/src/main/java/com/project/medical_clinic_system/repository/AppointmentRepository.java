package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    boolean existsByDoctorDoctorIDAndAppointmentDateTime(
            UUID doctorID,
            LocalDateTime appointmentDateTime
    );

    boolean existsByPatientPatientIDAndAppointmentDateTime(
            UUID patientID,
            LocalDateTime appointmentDateTime
    );
}