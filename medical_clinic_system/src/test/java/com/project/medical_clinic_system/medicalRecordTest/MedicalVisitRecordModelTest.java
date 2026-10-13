package com.project.medical_clinic_system.medicalRecordTest;

import com.project.medical_clinic_system.model.MedicalVisitRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MedicalVisitRecordModelTest {

    @Test
    void constructor_setsAllFields() {
        UUID patient = UUID.randomUUID();
        UUID doctor = UUID.randomUUID();
        UUID appointment = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 4, 4);

        MedicalVisitRecord r = new MedicalVisitRecord(patient, doctor, appointment, "Flu", "Rest", date);

        assertEquals(patient, r.getPatientID());
        assertEquals(doctor, r.getDoctorID());
        assertEquals(appointment, r.getAppointmentID());
        assertEquals("Flu", r.getDiagnosis());
        assertEquals("Rest", r.getMedicalNotes());
        assertEquals(date, r.getRecordCreationDate());
        assertNull(r.getId());
    }
}
