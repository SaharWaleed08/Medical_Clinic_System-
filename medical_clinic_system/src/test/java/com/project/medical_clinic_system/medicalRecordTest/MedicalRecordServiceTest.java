package com.project.medical_clinic_system.medicalRecordTest;

import com.project.medical_clinic_system.dto.request.CreateMedicalRecordRequest;
import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.MedicalRecordMapper;
import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.model.Appointment;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.MedicalVisitRecord;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.model.Specialization;
import com.project.medical_clinic_system.repository.AppointmentRepository;
import org.springframework.security.access.AccessDeniedException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private MedicalVisitRecord record() {
        return new MedicalVisitRecord(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Flu", "Rest and fluids", LocalDate.of(2026, 5, 5));
    }

    private Appointment appointmentOf(UUID doctorId, UUID patientId, UUID appointmentId) {
        Doctor doctor = new Doctor("Doctor One", "d@mail.com", "pw", "01012345678",
                new Specialization("Cardiology", "Heart"), "1234-5678", 5, BigDecimal.TEN);
        doctor.setId(doctorId);
        Patient patient = new Patient("Patient One", "p@mail.com", "pw", "01112345678",
                LocalDate.of(1990, 1, 1), Gender.MALE, LocalDateTime.of(2026, 1, 1, 0, 0));
        patient.setId(patientId);
        Appointment appointment = new Appointment(patient, doctor, LocalDateTime.of(2030, 1, 7, 10, 0), "Checkup");
        appointment.setId(appointmentId);
        return appointment;
    }

    @Test
    void createMedicalRecord_success_takesPatientFromAppointmentAndDoctorFromCaller() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 5, 5);
        CreateMedicalRecordRequest request = new CreateMedicalRecordRequest(appointmentId, "Flu", "Rest and fluids", date);
        MedicalRecordResponse expected = new MedicalRecordResponse(UUID.randomUUID(), patientId, appointmentId, "Flu", "Rest and fluids");
        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointmentOf(doctorId, patientId, appointmentId)));
        when(medicalRecordMapper.toResponse(any(MedicalVisitRecord.class))).thenReturn(expected);

        MedicalRecordResponse result = medicalRecordService.CreateMedicalRecord(request, doctorId);

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
    void createMedicalRecord_appointmentNotFound_throwsAndDoesNotSave() {
        UUID appointmentId = UUID.randomUUID();
        CreateMedicalRecordRequest request =
                new CreateMedicalRecordRequest(appointmentId, "Flu", "Rest", LocalDate.of(2026, 5, 5));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> medicalRecordService.CreateMedicalRecord(request, UUID.randomUUID()));

        assertEquals("Appointment not found", ex.getMessage());
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void createMedicalRecord_appointmentBelongsToAnotherDoctor_throwsAccessDenied() {
        UUID appointmentId = UUID.randomUUID();
        UUID ownerDoctorId = UUID.randomUUID();
        UUID callerDoctorId = UUID.randomUUID();
        CreateMedicalRecordRequest request =
                new CreateMedicalRecordRequest(appointmentId, "Flu", "Rest", LocalDate.of(2026, 5, 5));
        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointmentOf(ownerDoctorId, UUID.randomUUID(), appointmentId)));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> medicalRecordService.CreateMedicalRecord(request, callerDoctorId));

        assertEquals("This appointment does not belong to you", ex.getMessage());
        verify(medicalRecordRepository, never()).save(any());
        verifyNoInteractions(medicalRecordMapper);
    }

    @Test
    void findById_found_returnsResponse() {
        UUID id = UUID.randomUUID();
        MedicalVisitRecord record = record();
        MedicalRecordResponse expected = new MedicalRecordResponse(id, record.getPatientID(), record.getAppointmentID(), "Flu", "Rest and fluids");
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
                new MedicalRecordResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "a", "b"),
                new MedicalRecordResponse(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "c", "d"));
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

    @Test
    void findByPatientID_returnsOnlyThatPatientsRecordsMapped() {
        UUID patientId = UUID.randomUUID();
        List<MedicalVisitRecord> entities = List.of(record());
        List<MedicalRecordResponse> expected = List.of(mock(MedicalRecordResponse.class));
        when(medicalRecordRepository.findByPatientID(patientId)).thenReturn(entities);
        when(medicalRecordMapper.toResponse(entities)).thenReturn(expected);

        assertSame(expected, medicalRecordService.findByPatientID(patientId));
        verify(medicalRecordRepository, never()).findAll();
    }

    @Test
    void findByDoctorID_returnsOnlyThatDoctorsRecordsMapped() {
        UUID doctorId = UUID.randomUUID();
        List<MedicalVisitRecord> entities = List.of(record(), record());
        List<MedicalRecordResponse> expected = List.of(mock(MedicalRecordResponse.class), mock(MedicalRecordResponse.class));
        when(medicalRecordRepository.findByDoctorID(doctorId)).thenReturn(entities);
        when(medicalRecordMapper.toResponse(entities)).thenReturn(expected);

        assertSame(expected, medicalRecordService.findByDoctorID(doctorId));
        verify(medicalRecordRepository, never()).findAll();
    }

    @Test
    void findByPatientID_noRecords_returnsEmptyList() {
        UUID patientId = UUID.randomUUID();
        when(medicalRecordRepository.findByPatientID(patientId)).thenReturn(List.of());
        when(medicalRecordMapper.toResponse(List.<MedicalVisitRecord>of())).thenReturn(List.of());

        assertTrue(medicalRecordService.findByPatientID(patientId).isEmpty());
    }
}
