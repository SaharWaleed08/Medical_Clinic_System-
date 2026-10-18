package com.project.medical_clinic_system.adminTest;

import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.model.Admin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminModelTest {

    @Test
    void constructor_setsFieldsAndAdminRole() {
        Admin a = new Admin("Root", "root@mail.com", "pw", "01012345678");

        assertEquals("Root", a.getName());
        assertEquals("root@mail.com", a.getEmail());
        assertEquals("pw", a.getPassword());
        assertEquals("01012345678", a.getPhone());
        assertEquals(Role.ADMIN, a.getRole());
    }
}
