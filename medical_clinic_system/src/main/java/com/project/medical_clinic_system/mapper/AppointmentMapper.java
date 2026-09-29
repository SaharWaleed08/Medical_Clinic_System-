package com.project.medical_clinic_system.mapper;


import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.model.Appointment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(Appointment appointment) {

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentDateTime(),
                appointment.getReasonForVisit(),
                appointment.getStatus()
        );
    }
    public List<AppointmentResponse> toResponse(List<Appointment> appointments) {
        List<AppointmentResponse> responses = new ArrayList<>();

        for (Appointment appointment : appointments) {
            responses.add(toResponse(appointment));
        }

        return responses;
    }

}