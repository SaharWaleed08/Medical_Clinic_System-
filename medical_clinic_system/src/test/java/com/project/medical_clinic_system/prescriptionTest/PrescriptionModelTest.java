package com.project.medical_clinic_system.prescriptionTest;

import com.project.medical_clinic_system.model.Prescription;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrescriptionModelTest {

    @Test
    void constructor_setsAllFields() {
        UUID recordId = UUID.randomUUID();

        Prescription p = new Prescription(recordId, "Panadol", 500, 3, 7, "With water");

        assertEquals(recordId, p.getRecordID());
        assertEquals("Panadol", p.getMedicationName());
        assertEquals(500, p.getDosage());
        assertEquals(3, p.getFrequency());
        assertEquals(7, p.getTreatmentDuration());
        assertEquals("With water", p.getAdditionalInstruction());
    }
}
