package com.project.medical_clinic_system.doctorTest;

import com.project.medical_clinic_system.controller.get.DoctorGetController;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.AvailableSlotResponse;
import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.exception.InvalidDataException;
import com.project.medical_clinic_system.service.AppointmentService;
import com.project.medical_clinic_system.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** Direct-call tests: @PreAuthorize rules are not evaluated here. */
@ExtendWith(MockitoExtension.class)
class DoctorGetControllerTest {

    @Mock
    private DoctorService doctorService;
    @Mock
    private AppointmentService appointmentService;

    private DoctorGetController controller;

    @BeforeEach
    void setUp() {
        controller = new DoctorGetController(doctorService, appointmentService);
    }

    // ---------- getDoctors ----------

    @Test
    void getDoctors_passesNameAndSpecializationAndBuildsPageable() {
        when(doctorService.findDoctors(any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        controller.getDoctors("ahmed", "cardio", 1, 5, "name", "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(doctorService).findDoctors(eq("ahmed"), eq("cardio"), captor.capture());
        assertEquals(1, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
        assertEquals(Sort.Direction.ASC, captor.getValue().getSort().getOrderFor("name").getDirection());
    }

    @Test
    void getDoctors_directionDesc_sortsDescending() {
        when(doctorService.findDoctors(any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        controller.getDoctors(null, null, 0, 10, "yearsOfExperience", "desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(doctorService).findDoctors(eq(null), eq(null), captor.capture());
        assertEquals(Sort.Direction.DESC, captor.getValue().getSort().getOrderFor("yearsOfExperience").getDirection());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 11})
    void getDoctors_invalidSize_throwsInvalidData(int size) {
        assertThrows(InvalidDataException.class, () -> controller.getDoctors(null, null, 0, size, "name", "asc"));
        verifyNoInteractions(doctorService);
    }

    @Test
    void getDoctors_negativePage_throwsInvalidData() {
        assertThrows(InvalidDataException.class, () -> controller.getDoctors(null, null, -1, 10, "name", "asc"));
        verifyNoInteractions(doctorService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"password", "PASSWORD"})
    void getDoctors_sortByPassword_isRejected(String sortBy) {
        assertThrows(InvalidDataException.class, () -> controller.getDoctors(null, null, 0, 10, sortBy, "asc"));
        verifyNoInteractions(doctorService);
    }

    // ---------- getDoctorByID ----------

    @Test
    void getDoctorByID_delegatesToService() {
        UUID id = UUID.randomUUID();
        DoctorResponse expected = mock(DoctorResponse.class);
        when(doctorService.findDoctorByID(id)).thenReturn(expected);

        assertSame(expected, controller.getDoctorByID(id));
    }

    // ---------- findDoctorAppointments ----------

    @Test
    void findDoctorAppointments_buildsPageableAndDelegates() {
        UUID id = UUID.randomUUID();
        when(appointmentService.findDoctorAppointments(eq(id), any(Pageable.class))).thenReturn(Page.empty());

        controller.findDoctorAppointments(id, 0, 10, "appointmentDateTime", "desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(appointmentService).findDoctorAppointments(eq(id), captor.capture());
        assertEquals(Sort.Direction.DESC, captor.getValue().getSort().getOrderFor("appointmentDateTime").getDirection());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 11})
    void findDoctorAppointments_invalidSize_throwsInvalidData(int size) {
        UUID id = UUID.randomUUID();
        assertThrows(InvalidDataException.class,
                () -> controller.findDoctorAppointments(id, 0, size, "appointmentDateTime", "asc"));
        verifyNoInteractions(appointmentService);
    }

    @Test
    void findDoctorAppointments_negativePage_throwsInvalidData() {
        UUID id = UUID.randomUUID();
        assertThrows(InvalidDataException.class,
                () -> controller.findDoctorAppointments(id, -1, 10, "appointmentDateTime", "asc"));
        verifyNoInteractions(appointmentService);
    }

    @Test
    void findDoctorAppointments_sortByPassword_isRejected() {
        UUID id = UUID.randomUUID();
        assertThrows(InvalidDataException.class,
                () -> controller.findDoctorAppointments(id, 0, 10, "Password", "asc"));
        verifyNoInteractions(appointmentService);
    }

    // ---------- getAvailableSlots ----------

    @Test
    void getAvailableSlots_delegatesToAppointmentService() {
        UUID id = UUID.randomUUID();
        LocalDate date = LocalDate.of(2030, 1, 7);
        List<AvailableSlotResponse> expected = List.of(new AvailableSlotResponse(date.atTime(9, 0)));
        when(appointmentService.getAvailableSlots(id, date)).thenReturn(expected);

        assertSame(expected, controller.getAvailableSlots(id, date));
    }
}
