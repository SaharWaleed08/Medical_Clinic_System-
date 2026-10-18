package com.project.medical_clinic_system.patientTest;

import com.project.medical_clinic_system.controller.get.PatientRecordsController;
import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.dto.response.PrescriptionResponse;
import com.project.medical_clinic_system.service.MedicalRecordService;
import com.project.medical_clinic_system.service.PrescriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Direct-call tests: the @PreAuthorize "own data only" rule is not evaluated here. */
@ExtendWith(MockitoExtension.class)
class PatientRecordsControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;
    @Mock
    private PrescriptionService prescriptionService;

    private PatientRecordsController controller;

    @BeforeEach
    void setUp() {
        controller = new PatientRecordsController(medicalRecordService, prescriptionService);
    }

    @Test
    void getPatientRecords_delegatesToMedicalRecordService() {
        UUID patientId = UUID.randomUUID();
        List<MedicalRecordResponse> expected = List.of(mock(MedicalRecordResponse.class));
        when(medicalRecordService.findByPatientID(patientId)).thenReturn(expected);

        assertSame(expected, controller.getPatientRecords(patientId));
    }

    @Test
    void getPatientPrescriptions_delegatesToPrescriptionService() {
        UUID patientId = UUID.randomUUID();
        List<PrescriptionResponse> expected = List.of(mock(PrescriptionResponse.class));
        when(prescriptionService.findByPatientID(patientId)).thenReturn(expected);

        assertSame(expected, controller.getPatientPrescriptions(patientId));
    }
}
