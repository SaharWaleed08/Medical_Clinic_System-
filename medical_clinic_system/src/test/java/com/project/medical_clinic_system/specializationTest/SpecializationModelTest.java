package com.project.medical_clinic_system.specializationTest;

import com.project.medical_clinic_system.model.Specialization;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SpecializationModelTest {

    @Test
    void constructor_setsNameAndDescription() {
        Specialization s = new Specialization("Cardiology", "Heart diseases");

        assertEquals("Cardiology", s.getName());
        assertEquals("Heart diseases", s.getDescription());
        assertNull(s.getSpecializationID());
    }
}
