package com.project.medical_clinic_system.specializationTest;

import com.project.medical_clinic_system.dto.response.SpecializationResponse;
import com.project.medical_clinic_system.mapper.SpecializationMapper;
import com.project.medical_clinic_system.model.Specialization;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SpecializationMapperTest {

    private final SpecializationMapper mapper = new SpecializationMapper();

    private Specialization specialization(String name) {
        Specialization s = new Specialization(name, "desc-" + name);
        s.setSpecializationID(UUID.randomUUID());
        return s;
    }

    @Test
    void toResponse_mapsAllFields() {
        Specialization s = specialization("Cardiology");

        SpecializationResponse r = mapper.toResponse(s);

        assertEquals(s.getSpecializationID(), r.getSpecializationID());
        assertEquals("Cardiology", r.getName());
        assertEquals("desc-Cardiology", r.getDescription());
    }

    @Test
    void toResponse_list_mapsEveryElementInOrder() {
        List<SpecializationResponse> result = mapper.toResponse(List.of(specialization("A"), specialization("B")));

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getName());
        assertEquals("B", result.get(1).getName());
    }

    @Test
    void toResponse_emptyList_returnsEmptyList() {
        assertTrue(mapper.toResponse(List.<Specialization>of()).isEmpty());
    }
}
