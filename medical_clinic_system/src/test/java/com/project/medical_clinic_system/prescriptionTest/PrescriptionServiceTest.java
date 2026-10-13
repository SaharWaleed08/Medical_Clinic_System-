package com.project.medical_clinic_system.prescriptionTest;

import com.project.medical_clinic_system.dto.request.CreatePrescriptionRequest;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.PrescriptionMapper;
import com.project.medical_clinic_system.model.Prescription;
import com.project.medical_clinic_system.repository.PrescriptionRepository;
import com.project.medical_clinic_system.service.PrescriptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private PrescriptionMapper prescriptionMapper;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private PrescriptionResponse response(UUID recordId) {
        return new PrescriptionResponse(UUID.randomUUID(), recordId, "Panadol", 500, 3, 5, "After meals");
    }

    @Test
    void createPrescription_savesPrescriptionWithRequestData() {
        UUID recordId = UUID.randomUUID();
        CreatePrescriptionRequest request = new CreatePrescriptionRequest(recordId, "Panadol", 500, 3, 5, "After meals");
        PrescriptionResponse expected = response(recordId);
        when(prescriptionMapper.toResponse(any(Prescription.class))).thenReturn(expected);

        PrescriptionResponse result = prescriptionService.createPrescription(request);

        ArgumentCaptor<Prescription> captor = ArgumentCaptor.forClass(Prescription.class);
        verify(prescriptionRepository).save(captor.capture());
        Prescription saved = captor.getValue();

        assertSame(expected, result);
        assertEquals(recordId, saved.getRecordID());
        assertEquals("Panadol", saved.getMedicationName());
        assertEquals(500, saved.getDosage());
        assertEquals(3, saved.getFrequency());
        assertEquals(5, saved.getTreatmentDuration());
        assertEquals("After meals", saved.getAdditionalInstruction());
    }

    @Test
    void findPrescriptionById_found_returnsResponse() {
        UUID id = UUID.randomUUID();
        Prescription prescription = new Prescription(UUID.randomUUID(), "Panadol", 500, 3, 5, "After meals");
        PrescriptionResponse expected = response(prescription.getRecordID());
        when(prescriptionRepository.findById(id)).thenReturn(Optional.of(prescription));
        when(prescriptionMapper.toResponse(prescription)).thenReturn(expected);

        assertSame(expected, prescriptionService.findPrescriptionById(id));
    }

    @Test
    void findPrescriptionById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(prescriptionRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.findPrescriptionById(id));
        assertEquals("Prescription not found", ex.getMessage());
    }

    @Test
    void findAll_returnsMappedList() {
        List<Prescription> entities = List.of(new Prescription(UUID.randomUUID(), "A", 1, 1, 1, "x"));
        List<PrescriptionResponse> expected = List.of(response(UUID.randomUUID()));
        when(prescriptionRepository.findAll()).thenReturn(entities);
        when(prescriptionMapper.toResponse(entities)).thenReturn(expected);

        assertSame(expected, prescriptionService.findAll());
    }

    @Test
    void deletePrescriptionById_deletesAndReturnsMessage() {
        UUID id = UUID.randomUUID();

        assertEquals("Prescription is deleted", prescriptionService.deletePrescriptionById(id));
        verify(prescriptionRepository).deleteById(id);
    }
}
