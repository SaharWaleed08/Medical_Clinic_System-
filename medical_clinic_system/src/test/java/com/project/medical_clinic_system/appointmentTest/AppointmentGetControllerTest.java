package com.project.medical_clinic_system.appointmentTest;

import com.project.medical_clinic_system.controller.get.AppointmentGetController;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.enums.AppointmentStatus;
import com.project.medical_clinic_system.exception.InvalidDataException;
import com.project.medical_clinic_system.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** Direct-call tests: @PreAuthorize rules and URL mappings are not evaluated here. */
@ExtendWith(MockitoExtension.class)
class AppointmentGetControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentGetController controller;

    private final LocalDateTime start = LocalDateTime.of(2030, 1, 1, 0, 0);
    private final LocalDateTime end = LocalDateTime.of(2030, 1, 31, 23, 59);

    @Test
    void getAppointments_passesAllFiltersAndBuildsPageable() {
        when(appointmentService.findAppointments(any(), any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        controller.getAppointments(AppointmentStatus.CONFIRMED, start, end, 1, 5, "status", "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(appointmentService).findAppointments(
                eq(AppointmentStatus.CONFIRMED), eq(start), eq(end), captor.capture());
        assertEquals(1, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
        assertEquals(Sort.Direction.ASC, captor.getValue().getSort().getOrderFor("status").getDirection());
    }

    @Test
    void getAppointments_noFilters_passesNullsToService() {
        when(appointmentService.findAppointments(any(), any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        controller.getAppointments(null, null, null, 0, 10, "appointmentDateTime", "desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(appointmentService).findAppointments(eq(null), eq(null), eq(null), captor.capture());
        assertEquals(Sort.Direction.DESC,
                captor.getValue().getSort().getOrderFor("appointmentDateTime").getDirection());
    }

    @Test
    void getAppointments_unknownDirection_sortsDescending() {
        when(appointmentService.findAppointments(any(), any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        controller.getAppointments(null, null, null, 0, 10, "appointmentDateTime", "sideways");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(appointmentService).findAppointments(any(), any(), any(), captor.capture());
        assertEquals(Sort.Direction.DESC,
                captor.getValue().getSort().getOrderFor("appointmentDateTime").getDirection());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void getAppointments_sizeBoundaries_areAllowed(int size) {
        when(appointmentService.findAppointments(any(), any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        assertDoesNotThrow(() -> controller.getAppointments(null, null, null, 0, size, "status", "asc"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 11})
    void getAppointments_invalidSize_throwsInvalidData(int size) {
        assertThrows(InvalidDataException.class,
                () -> controller.getAppointments(null, null, null, 0, size, "status", "asc"));
        verifyNoInteractions(appointmentService);
    }

    @Test
    void getAppointments_negativePage_throwsInvalidData() {
        assertThrows(InvalidDataException.class,
                () -> controller.getAppointments(null, null, null, -1, 10, "status", "asc"));
        verifyNoInteractions(appointmentService);
    }

    @Test
    void findAppointmentByID_delegatesToService() {
        UUID id = UUID.randomUUID();
        AppointmentResponse expected = mock(AppointmentResponse.class);
        when(appointmentService.findAppointmentByID(id)).thenReturn(expected);

        assertSame(expected, controller.findAppointmentByID(id));
    }
}
