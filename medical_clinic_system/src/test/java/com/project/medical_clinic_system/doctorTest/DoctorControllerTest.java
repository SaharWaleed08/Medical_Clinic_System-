package com.project.medical_clinic_system.doctorTest;

import com.project.medical_clinic_system.controller.DoctorController;
import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.service.DoctorService;
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
class DoctorControllerTest {

    @Mock
    private DoctorService doctorService;

    private DoctorController controller;

    @BeforeEach
    void setUp() {
        controller = new DoctorController(doctorService);
    }

    @Test
    void createDoctor_delegatesToService() {
        CreateDoctorRequest request = mock(CreateDoctorRequest.class);
        DoctorResponse expected = mock(DoctorResponse.class);
        when(doctorService.createDoctor(request)).thenReturn(expected);

        assertSame(expected, controller.createDoctor(request));
    }

    @Test
    void updateDoctorByID_delegatesToService() {
        UUID id = UUID.randomUUID();
        CreateDoctorRequest request = mock(CreateDoctorRequest.class);
        DoctorResponse expected = mock(DoctorResponse.class);
        when(doctorService.updateDoctorByID(id, request)).thenReturn(expected);

        assertSame(expected, controller.updateDoctorByID(id, request));
    }

    @Test
    void deleteDoctorByID_returnsServiceMessage() {
        UUID id = UUID.randomUUID();
        when(doctorService.deleteDoctorByID(id)).thenReturn("Doctor is deleted");

        assertEquals("Doctor is deleted", controller.deleteDoctorByID(id));
    }
}
