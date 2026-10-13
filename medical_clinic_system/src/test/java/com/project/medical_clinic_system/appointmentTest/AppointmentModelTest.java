package com.project.medical_clinic_system.appointmentTest;

import com.project.medical_clinic_system.enums.AppointmentStatus;
import com.project.medical_clinic_system.model.Appointment;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Patient;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AppointmentModelTest {

    @Test
    void constructor_setsFieldsAndDefaultsStatusToPending() {
        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        LocalDateTime when = LocalDateTime.of(2030, 1, 7, 10, 0);

        Appointment a = new Appointment(patient, doctor, when, "Checkup");

        assertSame(patient, a.getPatient());
        assertSame(doctor, a.getDoctor());
        assertEquals(when, a.getAppointmentDateTime());
        assertEquals("Checkup", a.getReasonForVisit());
        assertEquals(AppointmentStatus.PENDING, a.getStatus());
    }
}
