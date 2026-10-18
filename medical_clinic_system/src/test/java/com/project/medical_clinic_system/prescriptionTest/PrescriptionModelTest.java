package com.project.medical_clinic_system.prescriptionTest;

import com.project.medical_clinic_system.model.MedicalVisitRecord;
import com.project.medical_clinic_system.model.Prescription;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PrescriptionModelTest {

    @Test
    void constructor_setsAllFields() {
        MedicalVisitRecord record = new MedicalVisitRecord();

        Prescription p = new Prescription(record, "Panadol", 500, 3, 7, "With water");

        assertSame(record, p.getMedicalVisitRecord());
        assertEquals("Panadol", p.getMedicationName());
        assertEquals(500, p.getDosage());
        assertEquals(3, p.getFrequency());
        assertEquals(7, p.getTreatmentDuration());
        assertEquals("With water", p.getAdditionalInstruction());
    }
}
