package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateAppointmentRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.AvailableSlotResponse;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.enums.AppointmentStatus;
import com.project.medical_clinic_system.mapper.AppointmentMapper;
import com.project.medical_clinic_system.model.Appointment;
import com.project.medical_clinic_system.model.Availability;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.repository.AppointmentRepository;
import com.project.medical_clinic_system.repository.AvailabilityRepository;
import com.project.medical_clinic_system.repository.DoctorRepository;
import com.project.medical_clinic_system.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
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

        Optional<Patient> patient = patientRepository.findById(request.getPatientID());
        Optional<Doctor> doctor = doctorRepository.findById(request.getDoctorID());

        if (patient.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        if (doctor.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        LocalDateTime appointmentDateTime = request.getAppointmentDateTime();

        if (!appointmentDateTime.isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Appointment must be in the future");
        }

        List<Availability> availabilities =
                availabilityRepository.findByDoctorIdAndDay(
                        doctor.get().getId(),
                        appointmentDateTime.getDayOfWeek()
                );

        boolean available = false;

        for (Availability availability : availabilities) {

            if (!appointmentDateTime.toLocalTime().isBefore(availability.getStartTime())
                    && !appointmentDateTime.toLocalTime().isAfter(availability.getEndTime())) {

                available = true;
                break;
            }
        }

        if (!available) {
            throw new RuntimeException("Doctor is not available at this time");
        }

        boolean doctorHasConflict =
                appointmentRepository.existsByDoctorIdAndAppointmentDateTime(
                        doctor.get().getId(),
                        appointmentDateTime
                );

        if (doctorHasConflict) {
            throw new RuntimeException("Doctor already has an appointment at this time");
        }

        boolean patientHasConflict =
                appointmentRepository.existsByPatientIdAndAppointmentDateTime(
                        patient.get().getId(),
                        appointmentDateTime
                );

        if (patientHasConflict) {
            throw new RuntimeException("Patient already has an appointment at this time");
        }

        Appointment appointment = new Appointment(
                patient.get(),
                doctor.get(),
                appointmentDateTime,
                request.getReasonForVisit()
        );

        appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(Optional.of(appointment));
    }

    public AppointmentResponse findAppointmentByID(UUID id) {

        Optional<Appointment> appointment = appointmentRepository.findById(id);

        return appointmentMapper.toResponse(appointment);
    }

    public Page<AppointmentResponse> findAppointments(String name, Pageable pageable) {

        Page<Appointment> appointments;

        if (name == null || name.isBlank()) {
            appointments = appointmentRepository.findAll(pageable);
        } else {
            appointments = appointmentRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        return appointments.map(appointment -> new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentDateTime(),
                appointment.getReasonForVisit(),
                appointment.getStatus()
        ));
    }

    public List<AppointmentResponse> findAllAppointments() {

        List<Appointment> appointments = appointmentRepository.findAll();

        return appointments.stream()
                .map(appointment -> appointmentMapper.toResponse(Optional.of(appointment)))
                .toList();
    }

    public List<AppointmentResponse> findPatientAppointments(UUID patientID) {

        if (patientRepository.findById(patientID).isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        List<Appointment> appointments =
                appointmentRepository.findByPatientId(patientID);

        return appointments.stream()
                .map(appointment -> appointmentMapper.toResponse(Optional.of(appointment)))
                .toList();
    }

    public List<AppointmentResponse> findDoctorAppointments(UUID doctorID) {

        if (doctorRepository.findById(doctorID).isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        List<Appointment> appointments =
                appointmentRepository.findByDoctorId(doctorID);

        return appointments.stream()
                .map(appointment -> appointmentMapper.toResponse(Optional.of(appointment)))
                .toList();
    }

    public List<AppointmentResponse> findAppointmentsByStatus(AppointmentStatus status) {

        List<Appointment> appointments =
                appointmentRepository.findByStatus(status);

        return appointments.stream()
                .map(appointment -> appointmentMapper.toResponse(Optional.of(appointment)))
                .toList();
    }

    public List<AppointmentResponse> findAppointmentsByDate(
            LocalDateTime start,
            LocalDateTime end) {

        List<Appointment> appointments =
                appointmentRepository.findByAppointmentDateTimeBetween(start, end);

        return appointments.stream()
                .map(appointment -> appointmentMapper.toResponse(Optional.of(appointment)))
                .toList();
    }

    public AppointmentResponse confirmAppointment(UUID id) {

        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (appointment.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        if (appointment.get().getStatus() != AppointmentStatus.PENDING) {
            throw new RuntimeException("Appointment cannot be confirmed");
        }

        appointment.get().setStatus(AppointmentStatus.CONFIRMED);

        appointmentRepository.save(appointment.get());

        return appointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse cancelAppointment(UUID id) {

        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (appointment.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        if (appointment.get().getStatus() != AppointmentStatus.PENDING
                && appointment.get().getStatus() != AppointmentStatus.CONFIRMED) {
            throw new RuntimeException("Appointment cannot be cancelled");
        }

        appointment.get().setStatus(AppointmentStatus.CANCELLED);

        appointmentRepository.save(appointment.get());

        return appointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse completeAppointment(UUID id) {

        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (appointment.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        if (appointment.get().getStatus() != AppointmentStatus.CONFIRMED) {
            throw new RuntimeException("Appointment cannot be completed");
        }

        appointment.get().setStatus(AppointmentStatus.COMPLETED);

        appointmentRepository.save(appointment.get());

        return appointmentMapper.toResponse(appointment);
    }

    public AppointmentResponse markAppointmentAsNoShow(UUID id) {

        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (appointment.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        if (appointment.get().getStatus() != AppointmentStatus.CONFIRMED) {
            throw new RuntimeException("Appointment cannot be marked as no-show");
        }

        appointment.get().setStatus(AppointmentStatus.NO_SHOW);

        appointmentRepository.save(appointment.get());

        return appointmentMapper.toResponse(appointment);
    }

    public List<AvailableSlotResponse> getAvailableSlots(
            UUID doctorId,
            LocalDate date) {

        if (doctorRepository.findById(doctorId).isEmpty()) {
            throw new RuntimeException("Doctor not found");
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