package com.project.medical_clinic_system.appointmentTest;

import com.project.medical_clinic_system.controller.AppointmentController;
import com.project.medical_clinic_system.dto.request.CreateAppointmentRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.service.AppointmentService;
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
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    private AppointmentController controller;

    @BeforeEach
    void setUp() {
        controller = new AppointmentController(appointmentService);
    }

    @Test
    void createAppointment_delegatesToService() {
        CreateAppointmentRequest request = mock(CreateAppointmentRequest.class);
        AppointmentResponse expected = mock(AppointmentResponse.class);
        when(appointmentService.createAppointment(request)).thenReturn(expected);

        assertSame(expected, controller.createAppointment(request));
    }

    @Test
    void confirmAppointment_delegatesToService() {
        UUID id = UUID.randomUUID();
        AppointmentResponse expected = mock(AppointmentResponse.class);
        when(appointmentService.confirmAppointment(id)).thenReturn(expected);

        assertSame(expected, controller.confirmAppointment(id));
    }

    @Test
    void cancelAppointment_delegatesToService() {
        UUID id = UUID.randomUUID();
        AppointmentResponse expected = mock(AppointmentResponse.class);
        when(appointmentService.cancelAppointment(id)).thenReturn(expected);

        assertSame(expected, controller.cancelAppointment(id));
    }

    @Test
    void completeAppointment_delegatesToService() {
        UUID id = UUID.randomUUID();
        AppointmentResponse expected = mock(AppointmentResponse.class);
        when(appointmentService.completeAppointment(id)).thenReturn(expected);

        assertSame(expected, controller.completeAppointment(id));
    }

    @Test
    void markAppointmentAsNoShow_delegatesToService() {
        UUID id = UUID.randomUUID();
        AppointmentResponse expected = mock(AppointmentResponse.class);
        when(appointmentService.markAppointmentAsNoShow(id)).thenReturn(expected);

        assertSame(expected, controller.markAppointmentAsNoShow(id));
    }
}
