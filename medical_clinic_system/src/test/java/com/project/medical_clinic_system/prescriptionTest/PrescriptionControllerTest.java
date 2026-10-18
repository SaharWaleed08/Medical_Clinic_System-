package com.project.medical_clinic_system.prescriptionTest;

import com.project.medical_clinic_system.controller.PrescriptionController;
import com.project.medical_clinic_system.controller.get.PrescriptionGetController;
import com.project.medical_clinic_system.dto.request.CreatePrescriptionRequest;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.model.User;
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

    private User doctorUser(UUID id) {
        User doctor = new Admin("Doctor One", "d@mail.com", "pw", "01012345678");
        doctor.setId(id);
        return doctor;
    }

    @Test
    void createPrescription_passesAuthenticatedDoctorIdToService() {
        CreatePrescriptionRequest request = mock(CreatePrescriptionRequest.class);
        PrescriptionResponse expected = mock(PrescriptionResponse.class);
        UUID doctorId = UUID.randomUUID();
        when(prescriptionService.createPrescription(request, doctorId)).thenReturn(expected);

        assertSame(expected, controller.createPrescription(request, doctorUser(doctorId)));
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
    void getAll_returnsOnlyTheAuthenticatedDoctorsPrescriptions() {
        UUID doctorId = UUID.randomUUID();
        List<PrescriptionResponse> expected = List.of(mock(PrescriptionResponse.class));
        when(prescriptionService.findByDoctorID(doctorId)).thenReturn(expected);

        assertSame(expected, getController.getAll(doctorUser(doctorId)));
    }
}
