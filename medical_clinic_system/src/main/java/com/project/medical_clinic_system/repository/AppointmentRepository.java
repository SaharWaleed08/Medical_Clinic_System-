package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.enums.AppointmentStatus;
import com.project.medical_clinic_system.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    boolean existsByDoctorIdAndAppointmentDateTime(
            UUID doctorId,
            LocalDateTime appointmentDateTime
    );

    boolean existsByPatientIdAndAppointmentDateTime(
            UUID patientId,
            LocalDateTime appointmentDateTime
    );

    List<Appointment> findByPatientId(UUID patientId);

    List<Appointment> findByDoctorId(UUID doctorId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByAppointmentDateTimeBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
            UUID doctorId,
            LocalDateTime appointmentDateTime,
            AppointmentStatus status
    );
}