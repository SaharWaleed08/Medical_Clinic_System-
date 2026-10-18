package com.project.medical_clinic_system.doctorTest;

import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.exception.DuplicateResourceException;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.DoctorMapper;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Specialization;
import com.project.medical_clinic_system.repository.DoctorRepository;
import com.project.medical_clinic_system.repository.SpecializationRepository;
import com.project.medical_clinic_system.repository.UserRepository;
import com.project.medical_clinic_system.service.DoctorService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SpecializationRepository specializationRepository;

    @InjectMocks
    private DoctorService doctorService;

    private final Pageable pageable = PageRequest.of(0, 10);
    private final UUID specializationId = UUID.randomUUID();
    private final Specialization specialization = new Specialization("Cardiology", "Heart diseases");

    private CreateDoctorRequest request() {
        return new CreateDoctorRequest("Doctor Ahmed Hassan", "doc@mail.com", "Passw0rd@1",
                "01012345678", "1234-5678", 8, new BigDecimal("300"), specializationId);
    }

    private Doctor doctor(UUID id) {
        Doctor d = new Doctor("Doctor Ahmed Hassan", "doc@mail.com", "encoded", "01012345678",
                specialization, "1234-5678", 8, new BigDecimal("300"));
        d.setId(id);
        return d;
    }

    private DoctorResponse responseOf(Doctor d) {
        return new DoctorResponse(d.getId(), d.getName(), d.getEmail(), "Cardiology");
    }

    // ---------- createDoctor ----------

    @Test
    void createDoctor_success_savesDoctorWithSpecializationAndEncodedPassword() {
        CreateDoctorRequest request = request();
        DoctorResponse expected = new DoctorResponse(UUID.randomUUID(), request.getName(), request.getEmail(), "Cardiology");

        when(specializationRepository.findById(specializationId)).thenReturn(Optional.of(specialization));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(doctorMapper.toResponse(any(Doctor.class))).thenReturn(expected);

        DoctorResponse result = doctorService.createDoctor(request);

        ArgumentCaptor<Doctor> captor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository).save(captor.capture());
        Doctor saved = captor.getValue();

        assertSame(expected, result);
        assertEquals("encoded-pass", saved.getPassword());
        assertEquals(Role.DOCTOR, saved.getRole());
        assertSame(specialization, saved.getSpecialization());
        assertEquals("1234-5678", saved.getLicenseNumber());
        assertEquals(8, saved.getYearsOfExperience());
        assertEquals(new BigDecimal("300"), saved.getConsultationFee());
    }

    @Test
    void createDoctor_specializationNotFound_throwsAndDoesNotSave() {
        CreateDoctorRequest request = request();
        when(specializationRepository.findById(specializationId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> doctorService.createDoctor(request));

        assertEquals("Specialization not found", ex.getMessage());
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void createDoctor_duplicateEmail_throwsAndDoesNotSave() {
        CreateDoctorRequest request = request();
        when(specializationRepository.findById(specializationId)).thenReturn(Optional.of(specialization));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> doctorService.createDoctor(request));

        verify(doctorRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    // ---------- findDoctors (name + specialization filters) ----------

    @Test
    void findDoctors_noFilters_usesFindAll() {
        Doctor d = doctor(UUID.randomUUID());
        when(doctorRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(d), pageable, 1));
        when(doctorMapper.toResponse(d)).thenReturn(responseOf(d));

        Page<DoctorResponse> result = doctorService.findDoctors(null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(doctorRepository, never()).findByNameContainingIgnoreCase(any(), any());
        verify(doctorRepository, never()).findBySpecializationNameContainingIgnoreCase(any(), any());
    }

    @Test
    void findDoctors_blankFilters_usesFindAll() {
        when(doctorRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        assertTrue(doctorService.findDoctors("  ", "", pageable).isEmpty());
        verify(doctorRepository, never()).findByNameContainingIgnoreCase(any(), any());
        verify(doctorRepository, never()).findBySpecializationNameContainingIgnoreCase(any(), any());
    }

    @Test
    void findDoctors_onlyName_usesNameQuery() {
        Doctor d = doctor(UUID.randomUUID());
        when(doctorRepository.findByNameContainingIgnoreCase("ahmed", pageable))
                .thenReturn(new PageImpl<>(List.of(d), pageable, 1));
        when(doctorMapper.toResponse(d)).thenReturn(responseOf(d));

        Page<DoctorResponse> result = doctorService.findDoctors("ahmed", null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(doctorRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void findDoctors_onlySpecialization_usesSpecializationQuery() {
        Doctor d = doctor(UUID.randomUUID());
        when(doctorRepository.findBySpecializationNameContainingIgnoreCase("cardio", pageable))
                .thenReturn(new PageImpl<>(List.of(d), pageable, 1));
        when(doctorMapper.toResponse(d)).thenReturn(responseOf(d));

        Page<DoctorResponse> result = doctorService.findDoctors(null, "cardio", pageable);

        assertEquals(1, result.getTotalElements());
        verify(doctorRepository, never()).findAll(any(Pageable.class));
        verify(doctorRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void findDoctors_nameAndSpecialization_usesCombinedQuery() {
        Doctor d = doctor(UUID.randomUUID());
        when(doctorRepository.findByNameContainingIgnoreCaseAndSpecializationNameContainingIgnoreCase(
                "ahmed", "cardio", pageable)).thenReturn(new PageImpl<>(List.of(d), pageable, 1));
        when(doctorMapper.toResponse(d)).thenReturn(responseOf(d));

        Page<DoctorResponse> result = doctorService.findDoctors("ahmed", "cardio", pageable);

        assertEquals(1, result.getTotalElements());
        verify(doctorRepository, never()).findAll(any(Pageable.class));
        verify(doctorRepository, never()).findByNameContainingIgnoreCase(any(), any());
        verify(doctorRepository, never()).findBySpecializationNameContainingIgnoreCase(any(), any());
    }

    // ---------- findDoctorByID ----------

    @Test
    void findDoctorByID_found_returnsResponse() {
        UUID id = UUID.randomUUID();
        Doctor d = doctor(id);
        DoctorResponse expected = responseOf(d);
        when(doctorRepository.findById(id)).thenReturn(Optional.of(d));
        when(doctorMapper.toResponse(d)).thenReturn(expected);

        assertSame(expected, doctorService.findDoctorByID(id));
    }

    @Test
    void findDoctorByID_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(doctorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.findDoctorByID(id));
    }

    // ---------- updateDoctorByID ----------

    @Test
    void updateDoctorByID_found_updatesAllFieldsAndSaves() {
        UUID id = UUID.randomUUID();
        Doctor existing = doctor(id);
        CreateDoctorRequest request = new CreateDoctorRequest("Doctor Mona Samir Ali", "new@mail.com",
                "NewPass@123", "01198765432", "8765-4321", 12, new BigDecimal("500"), specializationId);
        DoctorResponse expected = new DoctorResponse(id, request.getName(), request.getEmail(), "Cardiology");

        when(doctorRepository.findById(id)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("NewPass@123")).thenReturn("new-encoded");
        when(doctorMapper.toResponse(existing)).thenReturn(expected);

        DoctorResponse result = doctorService.updateDoctorByID(id, request);

        assertSame(expected, result);
        assertEquals("Doctor Mona Samir Ali", existing.getName());
        assertEquals("new@mail.com", existing.getEmail());
        assertEquals("new-encoded", existing.getPassword());
        assertEquals("01198765432", existing.getPhone());
        assertEquals("8765-4321", existing.getLicenseNumber());
        assertEquals(12, existing.getYearsOfExperience());
        assertEquals(new BigDecimal("500"), existing.getConsultationFee());
        verify(doctorRepository).save(existing);
    }

    @Test
    void updateDoctorByID_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(doctorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.updateDoctorByID(id, request()));
        verify(doctorRepository, never()).save(any());
    }

    // ---------- deleteDoctorByID ----------

    @Test
    void deleteDoctorByID_deletesAndReturnsMessage() {
        UUID id = UUID.randomUUID();

        assertEquals("Doctor is deleted", doctorService.deleteDoctorByID(id));
        verify(doctorRepository).deleteById(id);
    }
}
