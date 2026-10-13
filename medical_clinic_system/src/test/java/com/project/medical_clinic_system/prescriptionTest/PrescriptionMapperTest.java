package com.project.medical_clinic_system.prescriptionTest;

import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.mapper.PrescriptionMapper;
import com.project.medical_clinic_system.model.Prescription;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionMapperTest {

    private final PrescriptionMapper mapper = new PrescriptionMapper();

    private Prescription prescription(String med) {
        Prescription p = new Prescription(UUID.randomUUID(), med, 500, 3, 7, "With water");
        p.setId(UUID.randomUUID());
        return p;
    }

    @Test
    void toResponse_mapsAllFields() {
        Prescription p = prescription("Panadol");

        PrescriptionResponse r = mapper.toResponse(p);

        assertEquals(p.getId(), r.getPrescriptionID());
        assertEquals(p.getRecordID(), r.getRecordID());
        assertEquals("Panadol", r.getMedicationName());
        assertEquals(500, r.getDosage());
        assertEquals(3, r.getFrequency());
        assertEquals(7, r.getTreatmentDuration());
        assertEquals("With water", r.getAdditionalInstruction());
    }

    @Test
    void toResponse_list_mapsEveryElementInOrder() {
        List<PrescriptionResponse> result = mapper.toResponse(List.of(prescription("A"), prescription("B")));

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getMedicationName());
        assertEquals("B", result.get(1).getMedicationName());
    }

    @Test
    void toResponse_emptyList_returnsEmptyList() {
        assertTrue(mapper.toResponse(List.<Prescription>of()).isEmpty());
    }
}
