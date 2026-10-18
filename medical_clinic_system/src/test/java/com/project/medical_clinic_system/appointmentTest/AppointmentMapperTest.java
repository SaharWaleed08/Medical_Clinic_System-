package com.project.medical_clinic_system.appointmentTest;

import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.enums.AppointmentStatus;
import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.mapper.AppointmentMapper;
import com.project.medical_clinic_system.model.Appointment;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.model.Specialization;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentMapperTest {

    private final AppointmentMapper mapper = new AppointmentMapper();

    private Appointment appointment(String reason) {
        Patient p = new Patient("Patient One", "p@mail.com", "pw", "01012345678",
                LocalDate.of(1990, 1, 1), Gender.FEMALE, LocalDateTime.of(2026, 1, 1, 0, 0));
        p.setId(UUID.randomUUID());
        Doctor d = new Doctor("Doctor One", "d@mail.com", "pw", "01112345678", new Specialization("Cardiology", "Heart diseases"), "1234-5678", 3, BigDecimal.TEN);
        d.setId(UUID.randomUUID());
        Appointment a = new Appointment(p, d, LocalDateTime.of(2030, 1, 7, 10, 0), reason);
        a.setId(UUID.randomUUID());
        return a;
    }

    @Test
    void toResponse_mapsAllFields() {
        Appointment a = appointment("Checkup");

        AppointmentResponse r = mapper.toResponse(a);

        assertEquals(a.getId(), r.getId());
        assertEquals(a.getPatient().getId(), r.getPatientID());
        assertEquals("Patient One", r.getPatientName());
        assertEquals(a.getDoctor().getId(), r.getDoctorID());
        assertEquals("Doctor One", r.getDoctorName());
        assertEquals(LocalDateTime.of(2030, 1, 7, 10, 0), r.getAppointmentDateTime());
        assertEquals("Checkup", r.getReasonForVisit());
        assertEquals(AppointmentStatus.PENDING, r.getStatus());
    }

    @Test
    void toResponse_list_mapsEveryElementInOrder() {
        List<AppointmentResponse> result = mapper.toResponse(List.of(appointment("a"), appointment("b")));

        assertEquals(2, result.size());
        assertEquals("a", result.get(0).getReasonForVisit());
        assertEquals("b", result.get(1).getReasonForVisit());
    }

    @Test
    void toResponse_emptyList_returnsEmptyList() {
        assertTrue(mapper.toResponse(List.<Appointment>of()).isEmpty());
    }
}
