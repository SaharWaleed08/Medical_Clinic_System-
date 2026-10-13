package com.project.medical_clinic_system.availabilityTest;

import com.project.medical_clinic_system.dto.response.AvailabilityResponse;
import com.project.medical_clinic_system.mapper.AvailabilityMapper;
import com.project.medical_clinic_system.model.Availability;
import com.project.medical_clinic_system.model.Doctor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityMapperTest {

    private final AvailabilityMapper mapper = new AvailabilityMapper();

    private Availability availability(DayOfWeek day) {
        Doctor d = new Doctor("Doctor One", "d@mail.com", "pw", "01012345678", "1234-5678", 5, BigDecimal.TEN);
        d.setId(UUID.randomUUID());
        Availability a = new Availability(d, day, LocalTime.of(9, 0), LocalTime.of(17, 0));
        a.setId(UUID.randomUUID());
        return a;
    }

    @Test
    void toResponse_mapsAllFields() {
        Availability a = availability(DayOfWeek.WEDNESDAY);

        AvailabilityResponse r = mapper.toResponse(a);

        assertEquals(a.getId(), r.getAvailabilityID());
        assertEquals(a.getDoctor().getId(), r.getDoctorID());
        assertEquals("Doctor One", r.getName());
        assertEquals(DayOfWeek.WEDNESDAY, r.getDay());
        assertEquals(LocalTime.of(9, 0), r.getStartTime());
        assertEquals(LocalTime.of(17, 0), r.getEndTime());
    }

    @Test
    void toResponse_list_mapsEveryElementInOrder() {
        List<AvailabilityResponse> result =
                mapper.toResponse(List.of(availability(DayOfWeek.MONDAY), availability(DayOfWeek.FRIDAY)));

        assertEquals(2, result.size());
        assertEquals(DayOfWeek.MONDAY, result.get(0).getDay());
        assertEquals(DayOfWeek.FRIDAY, result.get(1).getDay());
    }

    @Test
    void toResponse_emptyList_returnsEmptyList() {
        assertTrue(mapper.toResponse(List.<Availability>of()).isEmpty());
    }
}
