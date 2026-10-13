package com.project.medical_clinic_system.patientTest;

import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.mapper.PatientMapper;
import com.project.medical_clinic_system.model.Patient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PatientMapperTest {

    private final PatientMapper mapper = new PatientMapper();

    private Patient patient(String name) {
        Patient p = new Patient(name, name + "@mail.com", "pw", "01012345678",
                LocalDate.of(2000, 1, 1), Gender.MALE, LocalDateTime.of(2026, 1, 1, 10, 0));
        p.setId(UUID.randomUUID());
        return p;
    }

    @Test
    void toResponse_mapsAllFields() {
        Patient p = patient("ahmed");

        PatientResponse r = mapper.toResponse(p);

        assertEquals(p.getId(), r.getPatientID());
        assertEquals(p.getName(), r.getName());
        assertEquals(p.getEmail(), r.getEmail());
        assertEquals(p.getRegistrationDate(), r.getRegistrationDate());
    }

    @Test
    void toResponse_list_mapsEveryElementInOrder() {
        List<PatientResponse> result = mapper.toResponse(List.of(patient("a"), patient("b")));

        assertEquals(2, result.size());
        assertEquals("a", result.get(0).getName());
        assertEquals("b", result.get(1).getName());
    }

    @Test
    void toResponse_emptyList_returnsEmptyList() {
        assertTrue(mapper.toResponse(List.<Patient>of()).isEmpty());
    }
}
