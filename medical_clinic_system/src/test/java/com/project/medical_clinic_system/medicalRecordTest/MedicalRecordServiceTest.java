package com.project.medical_clinic_system.medicalRecordTest;

import com.project.medical_clinic_system.dto.request.CreateMedicalRecordRequest;
import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.MedicalRecordMapper;
import com.project.medical_clinic_system.model.MedicalVisitRecord;
import com.project.medical_clinic_system.repository.MedicalRecordRepository;
import com.project.medical_clinic_system.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
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
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private MedicalVisitRecord record() {
        return new MedicalVisitRecord(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Flu", "Rest and fluids", LocalDate.of(2026, 5, 5));
    }

    @Test
    void createMedicalRecord_savesRecordWithRequestData() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 5, 5);
        CreateMedicalRecordRequest request = new CreateMedicalRecordRequest(patientId, doctorId, appointmentId,
                "Flu", "Rest and fluids", date);
        MedicalRecordResponse expected = new MedicalRecordResponse(UUID.randomUUID(), patientId, "Flu", "Rest and fluids");
        when(medicalRecordMapper.toResponse(any(MedicalVisitRecord.class))).thenReturn(expected);

        MedicalRecordResponse result = medicalRecordService.CreateMedicalRecord(request);

        ArgumentCaptor<MedicalVisitRecord> captor = ArgumentCaptor.forClass(MedicalVisitRecord.class);
        verify(medicalRecordRepository).save(captor.capture());
        MedicalVisitRecord saved = captor.getValue();

        assertSame(expected, result);
        assertEquals(patientId, saved.getPatientID());
        assertEquals(doctorId, saved.getDoctorID());
        assertEquals(appointmentId, saved.getAppointmentID());
        assertEquals("Flu", saved.getDiagnosis());
        assertEquals("Rest and fluids", saved.getMedicalNotes());
        assertEquals(date, saved.getRecordCreationDate());
    }

    @Test
    void findById_found_returnsResponse() {
        UUID id = UUID.randomUUID();
        MedicalVisitRecord record = record();
        MedicalRecordResponse expected = new MedicalRecordResponse(id, record.getPatientID(), "Flu", "Rest and fluids");
        when(medicalRecordRepository.findById(id)).thenReturn(Optional.of(record));
        when(medicalRecordMapper.toResponse(record)).thenReturn(expected);

        assertSame(expected, medicalRecordService.findById(id));
    }

    @Test
    void findById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(medicalRecordRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> medicalRecordService.findById(id));
        assertEquals("Medical visit record not found", ex.getMessage());
    }

    @Test
    void findAll_returnsMappedList() {
        List<MedicalVisitRecord> entities = List.of(record(), record());
        List<MedicalRecordResponse> expected = List.of(
                new MedicalRecordResponse(UUID.randomUUID(), UUID.randomUUID(), "a", "b"),
                new MedicalRecordResponse(UUID.randomUUID(), UUID.randomUUID(), "c", "d"));
        when(medicalRecordRepository.findAll()).thenReturn(entities);
        when(medicalRecordMapper.toResponse(entities)).thenReturn(expected);

        assertSame(expected, medicalRecordService.findAll());
    }

    @Test
    void deleteMedicalRecordById_deletesAndReturnsMessage() {
        UUID id = UUID.randomUUID();

        assertEquals("Medical Record is deleted", medicalRecordService.deleteMedicalRecordById(id));
        verify(medicalRecordRepository).deleteById(id);
    }
}
