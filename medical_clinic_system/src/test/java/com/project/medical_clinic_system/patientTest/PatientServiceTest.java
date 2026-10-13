package com.project.medical_clinic_system.patientTest;

import com.project.medical_clinic_system.dto.request.CreatePatientRequest;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.exception.DuplicateResourceException;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.PatientMapper;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.repository.PatientRepository;
import com.project.medical_clinic_system.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientService patientService;

    private final Pageable pageable = PageRequest.of(0, 10);

    private CreatePatientRequest request() {
        return new CreatePatientRequest("Ahmed Mohamed Ali", "Passw0rd@1", "ahmed@mail.com",
                "01012345678", LocalDate.of(1995, 5, 20), Gender.MALE,
                LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    private Patient patient(UUID id) {
        Patient p = new Patient("Ahmed Mohamed Ali", "ahmed@mail.com", "encoded", "01012345678",
                LocalDate.of(1995, 5, 20), Gender.MALE, LocalDateTime.of(2026, 1, 1, 0, 0));
        p.setId(id);
        return p;
    }

    // ---------- createPatient ----------

    @Test
    void createPatient_success_savesEncodedPasswordAndReturnsResponse() {
        CreatePatientRequest request = request();
        PatientResponse expected = new PatientResponse(UUID.randomUUID(), request.getName(),
                request.getEmail(), request.getRegistrationDate());

        when(patientRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(patientMapper.toResponse(any(Patient.class))).thenReturn(expected);

        PatientResponse result = patientService.createPatient(request);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        Patient saved = captor.getValue();

        assertSame(expected, result);
        assertEquals("encoded-pass", saved.getPassword());
        assertEquals(request.getEmail(), saved.getEmail());
        assertEquals(Role.PATIENT, saved.getRole());
        assertEquals(request.getGender(), saved.getGender());
    }

    @Test
    void createPatient_duplicateEmail_throwsAndDoesNotSave() {
        CreatePatientRequest request = request();
        when(patientRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> patientService.createPatient(request));

        verify(patientRepository, never()).save(any());
    }

    // ---------- findPatients ----------

    @Test
    void findPatients_nullName_usesFindAll() {
        UUID id = UUID.randomUUID();
        Page<Patient> page = new PageImpl<>(List.of(patient(id)), pageable, 1);
        when(patientRepository.findAll(pageable)).thenReturn(page);

        Page<PatientResponse> result = patientService.findPatients(null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(id, result.getContent().get(0).getPatientID());
        verify(patientRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void findPatients_blankName_usesFindAll() {
        when(patientRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<PatientResponse> result = patientService.findPatients("   ", pageable);

        assertTrue(result.isEmpty());
        verify(patientRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void findPatients_withName_usesSearchQuery() {
        Page<Patient> page = new PageImpl<>(List.of(patient(UUID.randomUUID())), pageable, 1);
        when(patientRepository.findByNameContainingIgnoreCase("ahmed", pageable)).thenReturn(page);

        Page<PatientResponse> result = patientService.findPatients("ahmed", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Ahmed Mohamed Ali", result.getContent().get(0).getName());
        verify(patientRepository, never()).findAll(any(Pageable.class));
    }

    // ---------- findPatientByID ----------

    @Test
    void findPatientByID_found_returnsMappedResponse() {
        UUID id = UUID.randomUUID();
        Patient patient = patient(id);
        PatientResponse expected = new PatientResponse(id, patient.getName(), patient.getEmail(),
                patient.getRegistrationDate());
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(expected);

        assertSame(expected, patientService.findPatientByID(id));
    }

    @Test
    void patientNotFound() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.findPatientByID(id));
    }

    // ---------- updatePatientByID ----------

    @Test
    void updatePatientByID_found_updatesAllFields() {
        UUID id = UUID.randomUUID();
        Patient existing = patient(id);
        CreatePatientRequest request = new CreatePatientRequest("Mohamed Hassan Ibrahim", "NewPass@123",
                "new@mail.com", "01198765432", LocalDate.of(1990, 1, 1), Gender.FEMALE,
                LocalDateTime.of(2026, 2, 2, 0, 0));
        PatientResponse expected = new PatientResponse(id, request.getName(), request.getEmail(),
                request.getRegistrationDate());

        when(patientRepository.findById(id)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("NewPass@123")).thenReturn("new-encoded");
        when(patientMapper.toResponse(existing)).thenReturn(expected);

        PatientResponse result = patientService.updatePatientByID(id, request);

        assertSame(expected, result);
        assertEquals("Mohamed Hassan Ibrahim", existing.getName());
        assertEquals("new@mail.com", existing.getEmail());
        assertEquals("new-encoded", existing.getPassword());
        assertEquals("01198765432", existing.getPhone());
        assertEquals(LocalDate.of(1990, 1, 1), existing.getDateOfBirth());
        assertEquals(Gender.FEMALE, existing.getGender());
        assertEquals(LocalDateTime.of(2026, 2, 2, 0, 0), existing.getRegistrationDate());
    }

    @Test
    void updatePatientByID_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> patientService.updatePatientByID(id, request()));
        verify(passwordEncoder, never()).encode(any());
    }

    // ---------- deletePatientByID ----------

    @Test
    void deletePatientByID_deletesAndReturnsMessage() {
        UUID id = UUID.randomUUID();

        String message = patientService.deletePatientByID(id);

        assertEquals("Patient is deleted", message);
        verify(patientRepository).deleteById(id);
    }
}
