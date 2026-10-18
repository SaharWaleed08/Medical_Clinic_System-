package com.project.medical_clinic_system.doctorTest;

import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.mapper.DoctorMapper;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Specialization;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DoctorMapperTest {

    private final DoctorMapper mapper = new DoctorMapper();

    private Doctor doctor(String name) {
        Doctor d = new Doctor(name, name + "@mail.com", "pw", "01012345678", new Specialization("Cardiology", "Heart diseases"), "1234-5678", 5, BigDecimal.TEN);
        d.setId(UUID.randomUUID());
        return d;
    }

    @Test
    void toResponse_mapsAllFields() {
        Doctor d = doctor("ahmed");

        DoctorResponse r = mapper.toResponse(d);

        assertEquals(d.getId(), r.getId());
        assertEquals(d.getName(), r.getName());
        assertEquals(d.getEmail(), r.getEmail());
        assertEquals("Cardiology", r.getSpecialization());
    }

    @Test
    void toResponse_doctorWithoutSpecialization_returnsNullSpecialization() {
        Doctor d = doctor("noSpec");
        d.setSpecialization(null);

        DoctorResponse r = mapper.toResponse(d);

        assertNull(r.getSpecialization());
        assertEquals("noSpec", r.getName());
    }

    @Test
    void toResponse_list_mapsEveryElementInOrder() {
        List<DoctorResponse> result = mapper.toResponse(List.of(doctor("a"), doctor("b")));

        assertEquals(2, result.size());
        assertEquals("a", result.get(0).getName());
        assertEquals("b", result.get(1).getName());
    }

    @Test
    void toResponse_emptyList_returnsEmptyList() {
        assertTrue(mapper.toResponse(List.<Doctor>of()).isEmpty());
    }
}
