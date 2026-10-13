package com.project.medical_clinic_system.doctorTest;

import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.model.Doctor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoctorModelTest {

    @Test
    void constructor_setsFieldsAndDoctorRole() {
        Doctor d = new Doctor("Ahmed", "a@mail.com", "pw", "01012345678", "1234-5678", 7, new BigDecimal("250"));

        assertEquals("Ahmed", d.getName());
        assertEquals("a@mail.com", d.getEmail());
        assertEquals("1234-5678", d.getLicenseNumber());
        assertEquals(7, d.getYearsOfExperience());
        assertEquals(new BigDecimal("250"), d.getConsultationFee());
        assertEquals(Role.DOCTOR, d.getRole());
    }
}
