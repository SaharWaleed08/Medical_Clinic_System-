package com.project.medical_clinic_system.medicalRecordTest;

import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.mapper.MedicalRecordMapper;
import com.project.medical_clinic_system.model.MedicalVisitRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MedicalRecordMapperTest {

    private final MedicalRecordMapper mapper = new MedicalRecordMapper();

    private MedicalVisitRecord record(String diagnosis) {
        MedicalVisitRecord r = new MedicalVisitRecord(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                diagnosis, "notes-" + diagnosis, LocalDate.of(2026, 1, 1));
        r.setId(UUID.randomUUID());
        return r;
    }

    @Test
    void toResponse_mapsAllFields() {
        MedicalVisitRecord r = record("Flu");

        MedicalRecordResponse res = mapper.toResponse(r);

        assertEquals(r.getId(), res.getMedicalRecordID());
        assertEquals(r.getPatientID(), res.getPatientID());
        assertEquals(r.getAppointmentID(), res.getAppointmentID());
        assertEquals("Flu", res.getDiagnosis());
        assertEquals("notes-Flu", res.getMedicalNotes());
    }

    @Test
    void toResponse_list_mapsEveryElementInOrder() {
        List<MedicalRecordResponse> result = mapper.toResponse(List.of(record("A"), record("B")));

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getDiagnosis());
        assertEquals("B", result.get(1).getDiagnosis());
    }

    @Test
    void toResponse_emptyList_returnsEmptyList() {
        assertTrue(mapper.toResponse(List.<MedicalVisitRecord>of()).isEmpty());
    }
}
