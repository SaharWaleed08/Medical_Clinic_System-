package com.project.medical_clinic_system.mapper;

import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.model.Appointment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(Optional<Appointment> appointment) {

        return new AppointmentResponse(
                appointment.get().getId(),
                appointment.get().getPatient().getPatientID(),
                appointment.get().getPatient().getName(),
                appointment.get().getDoctor().getDoctorID(),
                appointment.get().getDoctor().getName(),
                appointment.get().getAppointmentDateTime(),
                appointment.get().getReasonForVisit(),
                appointment.get().getStatus()
        );
    }
}