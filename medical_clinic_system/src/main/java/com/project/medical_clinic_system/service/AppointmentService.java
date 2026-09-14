package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateAppointmentRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.mapper.AppointmentMapper;
import com.project.medical_clinic_system.model.Appointment;
import com.project.medical_clinic_system.model.Availability;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.repository.AppointmentRepository;
import com.project.medical_clinic_system.repository.AvailabilityRepository;
import com.project.medical_clinic_system.repository.DoctorRepository;
import com.project.medical_clinic_system.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
                availabilityRepository.findByDoctorDoctorIDAndDay(
                        doctor.get().getDoctorID(),
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
                appointmentRepository.existsByDoctorDoctorIDAndAppointmentDateTime(
                        doctor.get().getDoctorID(),
                        appointmentDateTime
                );

        if (doctorHasConflict) {
            throw new RuntimeException("Doctor already has an appointment at this time");
        }

        boolean patientHasConflict =
                appointmentRepository.existsByPatientPatientIDAndAppointmentDateTime(
                        patient.get().getPatientID(),
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
}