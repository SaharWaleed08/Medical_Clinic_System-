package com.project.medical_clinic_system.prescriptionTest;

import com.project.medical_clinic_system.dto.request.CreatePrescriptionRequest;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.PrescriptionMapper;
import com.project.medical_clinic_system.model.MedicalVisitRecord;
import com.project.medical_clinic_system.model.Prescription;
import com.project.medical_clinic_system.repository.MedicalRecordRepository;
import com.project.medical_clinic_system.repository.PrescriptionRepository;
import com.project.medical_clinic_system.service.PrescriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private PrescriptionMapper prescriptionMapper;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private MedicalVisitRecord record(UUID id) {
        return record(id, UUID.randomUUID());
    }

    private MedicalVisitRecord record(UUID id, UUID doctorId) {
        MedicalVisitRecord r = new MedicalVisitRecord(UUID.randomUUID(), doctorId, UUID.randomUUID(),
                "Flu", "Rest", LocalDate.of(2026, 5, 5));
        r.setId(id);
        return r;
    }

    private PrescriptionResponse response(UUID recordId) {
        return new PrescriptionResponse(UUID.randomUUID(), recordId, "Panadol", 500, 3, 5, "After meals");
    }

    // ---------- createPrescription ----------

    @Test
    void createPrescription_success_linksPrescriptionToMedicalRecord() {
        UUID recordId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        MedicalVisitRecord record = record(recordId, doctorId);
        CreatePrescriptionRequest request = new CreatePrescriptionRequest(recordId, "Panadol", 500, 3, 5, "After meals");
        PrescriptionResponse expected = response(recordId);
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(record));
        when(prescriptionMapper.toResponse(any(Prescription.class))).thenReturn(expected);

        PrescriptionResponse result = prescriptionService.createPrescription(request, doctorId);

        ArgumentCaptor<Prescription> captor = ArgumentCaptor.forClass(Prescription.class);
        verify(prescriptionRepository).save(captor.capture());
        Prescription saved = captor.getValue();

        assertSame(expected, result);
        assertSame(record, saved.getMedicalVisitRecord());
        assertEquals("Panadol", saved.getMedicationName());
        assertEquals(500, saved.getDosage());
        assertEquals(3, saved.getFrequency());
        assertEquals(5, saved.getTreatmentDuration());
        assertEquals("After meals", saved.getAdditionalInstruction());
    }

    @Test
    void createPrescription_medicalRecordNotFound_throwsAndDoesNotSave() {
        UUID recordId = UUID.randomUUID();
        CreatePrescriptionRequest request = new CreatePrescriptionRequest(recordId, "Panadol", 500, 3, 5, "After meals");
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> prescriptionService.createPrescription(request, UUID.randomUUID()));

        assertEquals("Medical record not found", ex.getMessage());
        verify(prescriptionRepository, never()).save(any());
    }

    @Test
    void createPrescription_recordBelongsToAnotherDoctor_throwsAccessDenied() {
        UUID recordId = UUID.randomUUID();
        UUID ownerDoctorId = UUID.randomUUID();
        UUID callerDoctorId = UUID.randomUUID();
        CreatePrescriptionRequest request = new CreatePrescriptionRequest(recordId, "Panadol", 500, 3, 5, "After meals");
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(record(recordId, ownerDoctorId)));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> prescriptionService.createPrescription(request, callerDoctorId));

        assertEquals("This record does not belong to you", ex.getMessage());
        verify(prescriptionRepository, never()).save(any());
        verifyNoInteractions(prescriptionMapper);
    }

    // ---------- find / delete ----------

    @Test
    void findPrescriptionById_found_returnsResponse() {
        UUID id = UUID.randomUUID();
        Prescription prescription = new Prescription(record(UUID.randomUUID()), "Panadol", 500, 3, 5, "After meals");
        PrescriptionResponse expected = response(UUID.randomUUID());
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
        List<Prescription> entities = List.of(new Prescription(record(UUID.randomUUID()), "A", 1, 1, 1, "x"));
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

    @Test
    void findByPatientID_returnsMappedPrescriptionsOfThatPatient() {
        UUID patientId = UUID.randomUUID();
        List<Prescription> entities = List.of(new Prescription(record(UUID.randomUUID()), "A", 1, 1, 1, "x"));
        List<PrescriptionResponse> expected = List.of(response(UUID.randomUUID()));
        when(prescriptionRepository.findByMedicalVisitRecord_PatientID(patientId)).thenReturn(entities);
        when(prescriptionMapper.toResponse(entities)).thenReturn(expected);

        assertSame(expected, prescriptionService.findByPatientID(patientId));
        verify(prescriptionRepository, never()).findAll();
    }

    @Test
    void findByDoctorID_returnsMappedPrescriptionsOfThatDoctor() {
        UUID doctorId = UUID.randomUUID();
        List<Prescription> entities = List.of(new Prescription(record(UUID.randomUUID()), "A", 1, 1, 1, "x"));
        List<PrescriptionResponse> expected = List.of(response(UUID.randomUUID()));
        when(prescriptionRepository.findByMedicalVisitRecord_DoctorID(doctorId)).thenReturn(entities);
        when(prescriptionMapper.toResponse(entities)).thenReturn(expected);

        assertSame(expected, prescriptionService.findByDoctorID(doctorId));
        verify(prescriptionRepository, never()).findAll();
    }
}
