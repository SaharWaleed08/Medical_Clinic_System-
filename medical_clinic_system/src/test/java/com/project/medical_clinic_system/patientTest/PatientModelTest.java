package com.project.medical_clinic_system.patientTest;

import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.model.Patient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PatientModelTest {

    @Test
    void constructor_setsFieldsAndPatientRole() {
        LocalDate dob = LocalDate.of(1999, 9, 9);
        LocalDateTime reg = LocalDateTime.of(2026, 3, 3, 12, 0);

        Patient p = new Patient("Ahmed", "a@mail.com", "pw", "01012345678", dob, Gender.FEMALE, reg);

        assertEquals("Ahmed", p.getName());
        assertEquals("a@mail.com", p.getEmail());
        assertEquals("pw", p.getPassword());
        assertEquals("01012345678", p.getPhone());
        assertEquals(dob, p.getDateOfBirth());
        assertEquals(Gender.FEMALE, p.getGender());
        assertEquals(reg, p.getRegistrationDate());
        assertEquals(Role.PATIENT, p.getRole());
        assertNull(p.getId());
    }
}
