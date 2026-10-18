package com.project.medical_clinic_system.availabilityTest;

import com.project.medical_clinic_system.model.Availability;
import com.project.medical_clinic_system.model.Doctor;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AvailabilityModelTest {

    @Test
    void constructor_setsAllFields() {
        Doctor doctor = new Doctor();

        Availability a = new Availability(doctor, DayOfWeek.SUNDAY, LocalTime.of(8, 30), LocalTime.of(12, 0));

        assertSame(doctor, a.getDoctor());
        assertEquals(DayOfWeek.SUNDAY, a.getDay());
        assertEquals(LocalTime.of(8, 30), a.getStartTime());
        assertEquals(LocalTime.of(12, 0), a.getEndTime());
    }
}
