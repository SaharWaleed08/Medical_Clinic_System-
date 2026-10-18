package com.project.medical_clinic_system.patientTest;

import com.project.medical_clinic_system.controller.PatientController;
import com.project.medical_clinic_system.dto.request.CreatePatientRequest;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Direct-call tests: @PreAuthorize rules are not evaluated here. */
@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    private PatientController controller;

    @BeforeEach
    void setUp() {
        controller = new PatientController(patientService);
    }

    @Test
    void createPatient_delegatesToService() {
        CreatePatientRequest request = mock(CreatePatientRequest.class);
        PatientResponse expected = mock(PatientResponse.class);
        when(patientService.createPatient(request)).thenReturn(expected);

        assertSame(expected, controller.createPatient(request));
    }

    @Test
    void updatePatientByID_delegatesToService() {
        UUID id = UUID.randomUUID();
        CreatePatientRequest request = mock(CreatePatientRequest.class);
        PatientResponse expected = mock(PatientResponse.class);
        when(patientService.updatePatientByID(id, request)).thenReturn(expected);

        assertSame(expected, controller.updatePatientByID(id, request));
    }

    @Test
    void deletePatientByID_returnsServiceMessage() {
        UUID id = UUID.randomUUID();
        when(patientService.deletePatientByID(id)).thenReturn("Patient is deleted");

        assertEquals("Patient is deleted", controller.deletePatientByID(id));
    }
}
