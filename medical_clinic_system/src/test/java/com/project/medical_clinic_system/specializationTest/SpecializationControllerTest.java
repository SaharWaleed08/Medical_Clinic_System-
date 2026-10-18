package com.project.medical_clinic_system.specializationTest;

import com.project.medical_clinic_system.controller.SpecializationController;
import com.project.medical_clinic_system.controller.get.SpecializationGetController;
import com.project.medical_clinic_system.dto.request.CreateSpecializationRequest;
import com.project.medical_clinic_system.dto.response.SpecializationResponse;
import com.project.medical_clinic_system.service.SpecializationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Direct-call tests: @PreAuthorize rules are not evaluated here. */
@ExtendWith(MockitoExtension.class)
class SpecializationControllerTest {

    @Mock
    private SpecializationService specializationService;

    private SpecializationController controller;
    private SpecializationGetController getController;

    @BeforeEach
    void setUp() {
        controller = new SpecializationController(specializationService);
        getController = new SpecializationGetController(specializationService);
    }

    @Test
    void createSpecialization_delegatesToService() {
        CreateSpecializationRequest request = mock(CreateSpecializationRequest.class);
        SpecializationResponse expected = mock(SpecializationResponse.class);
        when(specializationService.createSpecialization(request)).thenReturn(expected);

        assertSame(expected, controller.createSpecialization(request));
    }

    @Test
    void updateSpecializationByID_delegatesToService() {
        UUID id = UUID.randomUUID();
        CreateSpecializationRequest request = mock(CreateSpecializationRequest.class);
        SpecializationResponse expected = mock(SpecializationResponse.class);
        when(specializationService.updateSpecializationByID(id, request)).thenReturn(expected);

        assertSame(expected, controller.updateSpecializationByID(id, request));
    }

    @Test
    void deleteSpecializationByID_returnsServiceMessage() {
        UUID id = UUID.randomUUID();
        when(specializationService.deleteSpecializationByID(id)).thenReturn("Specialization is deleted");

        assertEquals("Specialization is deleted", controller.deleteSpecializationByID(id));
    }

    @Test
    void getSpecializationByID_delegatesToService() {
        UUID id = UUID.randomUUID();
        SpecializationResponse expected = mock(SpecializationResponse.class);
        when(specializationService.findSpecializationByID(id)).thenReturn(expected);

        assertSame(expected, getController.getSpecializationByID(id));
    }

    @Test
    void findAllSpecialization_delegatesToService() {
        List<SpecializationResponse> expected = List.of(mock(SpecializationResponse.class));
        when(specializationService.findAllSpecialization()).thenReturn(expected);

        assertSame(expected, getController.findAllSpecialization());
    }
}
