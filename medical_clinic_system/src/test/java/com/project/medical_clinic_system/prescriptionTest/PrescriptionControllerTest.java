package com.project.medical_clinic_system.prescriptionTest;

import com.project.medical_clinic_system.controller.PrescriptionController;
import com.project.medical_clinic_system.controller.get.PrescriptionGetController;
import com.project.medical_clinic_system.dto.request.CreatePrescriptionRequest;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.service.PrescriptionService;
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
class PrescriptionControllerTest {

    @Mock
    private PrescriptionService prescriptionService;

    private PrescriptionController controller;
    private PrescriptionGetController getController;

    @BeforeEach
    void setUp() {
        controller = new PrescriptionController(prescriptionService);
        getController = new PrescriptionGetController(prescriptionService);
    }

    @Test
    void createPrescription_delegatesToService() {
        CreatePrescriptionRequest request = mock(CreatePrescriptionRequest.class);
        PrescriptionResponse expected = mock(PrescriptionResponse.class);
        when(prescriptionService.createPrescription(request)).thenReturn(expected);

        assertSame(expected, controller.createPrescription(request));
    }

    @Test
    void deleteById_returnsServiceMessage() {
        UUID id = UUID.randomUUID();
        when(prescriptionService.deletePrescriptionById(id)).thenReturn("Prescription is deleted");

        assertEquals("Prescription is deleted", controller.deleteById(id));
    }

    @Test
    void getById_delegatesToService() {
        UUID id = UUID.randomUUID();
        PrescriptionResponse expected = mock(PrescriptionResponse.class);
        when(prescriptionService.findPrescriptionById(id)).thenReturn(expected);

        assertSame(expected, getController.getById(id));
    }

    @Test
    void getAll_delegatesToService() {
        List<PrescriptionResponse> expected = List.of(mock(PrescriptionResponse.class));
        when(prescriptionService.findAll()).thenReturn(expected);

        assertSame(expected, getController.getAll());
    }
}
