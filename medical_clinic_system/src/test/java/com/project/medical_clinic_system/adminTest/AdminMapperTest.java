package com.project.medical_clinic_system.adminTest;

import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.mapper.AdminMapper;
import com.project.medical_clinic_system.model.Admin;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AdminMapperTest {

    private final AdminMapper mapper = new AdminMapper();

    private Admin admin(String name) {
        Admin a = new Admin(name, name + "@mail.com", "pw", "01012345678");
        a.setId(UUID.randomUUID());
        return a;
    }

    @Test
    void toResponse_mapsAllFields() {
        Admin a = admin("root");

        AdminResponse r = mapper.toResponse(a);

        assertEquals(a.getId(), r.getId());
        assertEquals("root", r.getName());
        assertEquals("root@mail.com", r.getEmail());
    }

    @Test
    void toResponse_list_mapsEveryElementInOrder() {
        List<AdminResponse> result = mapper.toResponse(List.of(admin("a"), admin("b")));

        assertEquals(2, result.size());
        assertEquals("a", result.get(0).getName());
        assertEquals("b", result.get(1).getName());
    }

    @Test
    void toResponse_emptyList_returnsEmptyList() {
        assertTrue(mapper.toResponse(List.<Admin>of()).isEmpty());
    }
}
