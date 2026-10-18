package com.project.medical_clinic_system.medicalRecordTest;

import com.project.medical_clinic_system.controller.MedicalRecordController;
import com.project.medical_clinic_system.controller.get.MedicalRecordGetController;
import com.project.medical_clinic_system.dto.request.CreateMedicalRecordRequest;
import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.model.User;
import com.project.medical_clinic_system.service.MedicalRecordService;
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
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    private MedicalRecordController controller;
    private MedicalRecordGetController getController;

    @BeforeEach
    void setUp() {
        controller = new MedicalRecordController(medicalRecordService);
        getController = new MedicalRecordGetController(medicalRecordService);
    }

    @Test
    void createMedicalRecord_passesAuthenticatedDoctorIdToService() {
        CreateMedicalRecordRequest request = mock(CreateMedicalRecordRequest.class);
        MedicalRecordResponse expected = mock(MedicalRecordResponse.class);
        User doctor = new Admin("Doctor One", "d@mail.com", "pw", "01012345678");
        UUID doctorId = UUID.randomUUID();
        doctor.setId(doctorId);
        when(medicalRecordService.CreateMedicalRecord(request, doctorId)).thenReturn(expected);

        assertSame(expected, controller.CreateMedicalRecord(request, doctor));
    }

    @Test
    void deleteMedicalRecordById_returnsServiceMessage() {
        UUID id = UUID.randomUUID();
        when(medicalRecordService.deleteMedicalRecordById(id)).thenReturn("Medical Record is deleted");

        assertEquals("Medical Record is deleted", controller.deleteMedicalRecordById(id));
    }

    @Test
    void getMedicalRecordById_delegatesToService() {
        UUID id = UUID.randomUUID();
        MedicalRecordResponse expected = mock(MedicalRecordResponse.class);
        when(medicalRecordService.findById(id)).thenReturn(expected);

        assertSame(expected, getController.getMedicalRecordById(id));
    }

    @Test
    void getAll_delegatesToService() {
        List<MedicalRecordResponse> expected = List.of(mock(MedicalRecordResponse.class));
        when(medicalRecordService.findAll()).thenReturn(expected);

        assertSame(expected, getController.getAll());
    }
}
