package com.project.medical_clinic_system.doctorTest;

import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Specialization;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DoctorModelTest {

    @Test
    void constructor_setsFieldsSpecializationAndDoctorRole() {
        Specialization spec = new Specialization("Cardiology", "Heart diseases");

        Doctor d = new Doctor("Ahmed", "a@mail.com", "pw", "01012345678", spec, "1234-5678", 7, new BigDecimal("250"));

        assertEquals("Ahmed", d.getName());
        assertEquals("a@mail.com", d.getEmail());
        assertSame(spec, d.getSpecialization());
        assertEquals("1234-5678", d.getLicenseNumber());
        assertEquals(7, d.getYearsOfExperience());
        assertEquals(new BigDecimal("250"), d.getConsultationFee());
        assertEquals(Role.DOCTOR, d.getRole());
    }
}
