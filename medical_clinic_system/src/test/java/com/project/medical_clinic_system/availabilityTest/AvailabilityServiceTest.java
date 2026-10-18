package com.project.medical_clinic_system.availabilityTest;

import com.project.medical_clinic_system.dto.request.CreateAvailabilityRequest;
import com.project.medical_clinic_system.dto.response.AvailabilityResponse;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.AvailabilityMapper;
import com.project.medical_clinic_system.model.Availability;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.repository.AvailabilityRepository;
import com.project.medical_clinic_system.repository.DoctorRepository;
import com.project.medical_clinic_system.service.AvailabilityService;
import com.project.medical_clinic_system.model.Specialization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;
    @Mock
    private AvailabilityMapper availabilityMapper;
    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    private Doctor doctor(UUID id) {
        Doctor d = new Doctor("Doctor One", "d@mail.com", "pw", "01012345678", new Specialization("Cardiology", "Heart diseases"), "1234-5678", 5, BigDecimal.TEN);
        d.setId(id);
        return d;
    }

    private AvailabilityResponse response(UUID doctorId) {
        return new AvailabilityResponse(UUID.randomUUID(), doctorId, "Doctor One",
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
    }

    @Test
    void createAvailability_success_savesAvailabilityForDoctor() {
        UUID doctorId = UUID.randomUUID();
        Doctor doctor = doctor(doctorId);
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(doctorId, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(17, 0));
        AvailabilityResponse expected = response(doctorId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(availabilityMapper.toResponse(any(Availability.class))).thenReturn(expected);

        AvailabilityResponse result = availabilityService.createAvailability(request);

        ArgumentCaptor<Availability> captor = ArgumentCaptor.forClass(Availability.class);
        verify(availabilityRepository).save(captor.capture());
        Availability saved = captor.getValue();

        assertSame(expected, result);
        assertSame(doctor, saved.getDoctor());
        assertEquals(DayOfWeek.MONDAY, saved.getDay());
        assertEquals(LocalTime.of(9, 0), saved.getStartTime());
        assertEquals(LocalTime.of(17, 0), saved.getEndTime());
    }

    @Test
    void createAvailability_doctorNotFound_throwsAndDoesNotSave() {
        UUID doctorId = UUID.randomUUID();
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(doctorId, DayOfWeek.MONDAY,
                LocalTime.of(9, 0), LocalTime.of(17, 0));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> availabilityService.createAvailability(request));

        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void findAvailabilityByID_found_returnsResponse() {
        UUID id = UUID.randomUUID();
        Availability availability = new Availability(doctor(UUID.randomUUID()), DayOfWeek.TUESDAY,
                LocalTime.of(10, 0), LocalTime.of(14, 0));
        AvailabilityResponse expected = response(UUID.randomUUID());
        when(availabilityRepository.findById(id)).thenReturn(Optional.of(availability));
        when(availabilityMapper.toResponse(availability)).thenReturn(expected);

        assertSame(expected, availabilityService.findAvailabilityByID(id));
    }

    @Test
    void findAvailabilityByID_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(availabilityRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> availabilityService.findAvailabilityByID(id));
    }

    @Test
    void findAllAvailability_returnsMappedList() {
        List<Availability> entities = List.of(
                new Availability(doctor(UUID.randomUUID()), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)));
        List<AvailabilityResponse> expected = List.of(response(UUID.randomUUID()));
        when(availabilityRepository.findAll()).thenReturn(entities);
        when(availabilityMapper.toResponse(entities)).thenReturn(expected);

        assertSame(expected, availabilityService.findAllAvailability());
    }

    @Test
    void deleteAvailabilityByID_deletesAndReturnsMessage() {
        UUID id = UUID.randomUUID();

        assertEquals("Availability is deleted", availabilityService.deleteAvailabilityByID(id));
        verify(availabilityRepository).deleteById(id);
    }
}
