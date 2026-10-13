package com.project.medical_clinic_system.specializationTest;

import com.project.medical_clinic_system.dto.request.CreateSpecializationRequest;
import com.project.medical_clinic_system.dto.response.SpecializationResponse;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.SpecializationMapper;
import com.project.medical_clinic_system.model.Specialization;
import com.project.medical_clinic_system.repository.SpecializationRepository;
import com.project.medical_clinic_system.service.SpecializationService;
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
class SpecializationServiceTest {

    @Mock
    private SpecializationRepository specializationRepository;
    @Mock
    private SpecializationMapper specializationMapper;

    @InjectMocks
    private SpecializationService specializationService;

    @Test
    void createSpecialization_savesAndReturnsResponseWithSameData() {
        CreateSpecializationRequest request = new CreateSpecializationRequest("Cardiology", "Heart diseases");

        SpecializationResponse result = specializationService.createSpecialization(request);

        ArgumentCaptor<Specialization> captor = ArgumentCaptor.forClass(Specialization.class);
        verify(specializationRepository).save(captor.capture());

        assertEquals("Cardiology", captor.getValue().getName());
        assertEquals("Heart diseases", captor.getValue().getDescription());
        assertEquals("Cardiology", result.getName());
        assertEquals("Heart diseases", result.getDescription());
    }

    @Test
    void findSpecializationByID_found_returnsResponse() {
        UUID id = UUID.randomUUID();
        Specialization s = new Specialization("Cardiology", "Heart diseases");
        SpecializationResponse expected = new SpecializationResponse(id, "Cardiology", "Heart diseases");
        when(specializationRepository.findById(id)).thenReturn(Optional.of(s));
        when(specializationMapper.toResponse(s)).thenReturn(expected);

        assertSame(expected, specializationService.findSpecializationByID(id));
    }

    @Test
    void findSpecializationByID_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(specializationRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> specializationService.findSpecializationByID(id));
        assertEquals("Specialization not found", ex.getMessage());
    }

    @Test
    void findAllSpecialization_returnsMappedList() {
        List<Specialization> entities = List.of(new Specialization("A", "a"), new Specialization("B", "b"));
        List<SpecializationResponse> expected = List.of(
                new SpecializationResponse(UUID.randomUUID(), "A", "a"),
                new SpecializationResponse(UUID.randomUUID(), "B", "b"));
        when(specializationRepository.findAll()).thenReturn(entities);
        when(specializationMapper.toResponse(entities)).thenReturn(expected);

        assertSame(expected, specializationService.findAllSpecialization());
    }

    @Test
    void updateSpecializationByID_found_updatesFieldsAndSaves() {
        UUID id = UUID.randomUUID();
        Specialization existing = new Specialization("Old", "old description");
        CreateSpecializationRequest request = new CreateSpecializationRequest("Neurology", "Brain and nerves");
        SpecializationResponse expected = new SpecializationResponse(id, "Neurology", "Brain and nerves");
        when(specializationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(specializationMapper.toResponse(existing)).thenReturn(expected);

        SpecializationResponse result = specializationService.updateSpecializationByID(id, request);

        assertSame(expected, result);
        assertEquals("Neurology", existing.getName());
        assertEquals("Brain and nerves", existing.getDescription());
        verify(specializationRepository).save(existing);
    }

    @Test
    void updateSpecializationByID_notFound_throwsAndDoesNotSave() {
        UUID id = UUID.randomUUID();
        when(specializationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> specializationService.updateSpecializationByID(id, new CreateSpecializationRequest("x", "y")));
        verify(specializationRepository, never()).save(any());
    }

    @Test
    void deleteSpecializationByID_deletesAndReturnsMessage() {
        UUID id = UUID.randomUUID();

        assertEquals("Specialization is deleted", specializationService.deleteSpecializationByID(id));
        verify(specializationRepository).deleteById(id);
    }
}
