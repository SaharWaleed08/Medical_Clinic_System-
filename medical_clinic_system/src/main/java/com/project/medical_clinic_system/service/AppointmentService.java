package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateAppointmentRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.AvailableSlotResponse;
import com.project.medical_clinic_system.enums.AppointmentStatus;
import com.project.medical_clinic_system.exception.*;
import com.project.medical_clinic_system.mapper.AppointmentMapper;
import com.project.medical_clinic_system.model.Appointment;
import com.project.medical_clinic_system.model.Availability;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.repository.AppointmentRepository;
import com.project.medical_clinic_system.repository.AvailabilityRepository;
import com.project.medical_clinic_system.repository.DoctorRepository;
import com.project.medical_clinic_system.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              AvailabilityRepository availabilityRepository,
                              AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.availabilityRepository = availabilityRepository;
        this.appointmentMapper = appointmentMapper;
    }

    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {

        Patient patient = patientRepository.findById(request.getPatientID())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        Doctor doctor = doctorRepository.findById(request.getDoctorID())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        LocalDateTime appointmentDateTime = request.getAppointmentDateTime();

        boolean doctorHasConflict =
                appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                        doctor.getId(),
                        appointmentDateTime,
                        AppointmentStatus.CANCELLED
                );

        if (doctorHasConflict) {
            throw new AppointmentConflictException("Doctor already has an appointment at this time");
        }

        boolean patientHasConflict =
                appointmentRepository.existsByPatientIdAndAppointmentDateTimeAndStatusNot(
                        patient.getId(),
                        appointmentDateTime,
                        AppointmentStatus.CANCELLED
                );

        if (patientHasConflict) {
            throw new AppointmentConflictException("Patient already has an appointment at this time");
        }

        Appointment appointment = new Appointment(
                patient,
                doctor,
                appointmentDateTime,
                request.getReasonForVisit()
        );

        appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse findAppointmentByID(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        return appointmentMapper.toResponse(appointment);
    }

    public Page<AppointmentResponse> findPatientAppointments(UUID patientID, Pageable pageable) {

        if (patientRepository.findById(patientID).isEmpty()) {
            throw new ResourceNotFoundException("Patient not found");
        }

        Page<Appointment> appointments =
                appointmentRepository.findByPatientId(patientID, pageable);

        return appointments.map(appointmentMapper::toResponse);
    }

    public Page<AppointmentResponse> findDoctorAppointments(UUID doctorID, Pageable pageable) {

        if (doctorRepository.findById(doctorID).isEmpty()) {
            throw new ResourceNotFoundException("Doctor not found");
        }

        Page<Appointment> appointments =
                appointmentRepository.findByDoctorId(doctorID, pageable);

        return appointments.map(appointmentMapper::toResponse);
    }

    public Page<AppointmentResponse> findAppointments(AppointmentStatus status,
                                                      LocalDateTime start,
                                                      LocalDateTime end,
                                                      Pageable pageable) {
        boolean hasDates = start != null && end != null;
        Page<Appointment> result;
        if (status != null && hasDates) {
            result = appointmentRepository.findByStatusAndAppointmentDateTimeBetween(status, start, end, pageable);
        } else if (status != null) {
            result = appointmentRepository.findByStatus(status, pageable);
        } else if (hasDates) {
            result = appointmentRepository.findByAppointmentDateTimeBetween(start, end, pageable);
        } else {
            result = appointmentRepository.findAll(pageable);
        }
        return result.map(appointmentMapper::toResponse);
    }
    public AppointmentResponse confirmAppointment(UUID id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new InvalidStatusTransitionException("Appointment cannot be confirmed");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);

        appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse cancelAppointment(UUID id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));


        if (appointment.getStatus() != AppointmentStatus.PENDING
                && appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new InvalidStatusTransitionException("Appointment cannot be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse completeAppointment(UUID id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new InvalidStatusTransitionException("Appointment cannot be completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);

        appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse markAppointmentAsNoShow(UUID id) {

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new InvalidStatusTransitionException("Appointment cannot be marked as no-show");
        }

        appointment.setStatus(AppointmentStatus.NO_SHOW);

        appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(appointment);
    }

    public List<AvailableSlotResponse> getAvailableSlots(
            UUID doctorId,
            LocalDate date) {

        if (doctorRepository.findById(doctorId).isEmpty()) {
            throw new ResourceNotFoundException("Doctor not found");
        }

        List<Availability> availabilities =
                availabilityRepository.findByDoctorIdAndDay(
                        doctorId,
                        date.getDayOfWeek()
                );

        List<AvailableSlotResponse> availableSlots = new ArrayList<>();

        for (Availability availability : availabilities) {

            LocalDateTime start = LocalDateTime.of(
                    date,
                    availability.getStartTime()
            );

            LocalDateTime end = LocalDateTime.of(
                    date,
                    availability.getEndTime()
            );

            LocalDateTime current = start;

            while (current.isBefore(end)) {

                boolean booked =
                        appointmentRepository
                                .existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                                        doctorId,
                                        current,
                                        AppointmentStatus.CANCELLED
                                );

                if (!booked) {
                    availableSlots.add(
                            new AvailableSlotResponse(current)
                    );
                }

                current = current.plusMinutes(30);
            }
        }

        return availableSlots;
    }
}