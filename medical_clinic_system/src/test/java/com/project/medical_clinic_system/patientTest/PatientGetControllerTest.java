package com.project.medical_clinic_system.patientTest;

import com.project.medical_clinic_system.controller.get.PatientGetController;
import com.project.medical_clinic_system.dto.response.PatientResponse;
import com.project.medical_clinic_system.exception.InvalidDataException;
import com.project.medical_clinic_system.service.AppointmentService;
import com.project.medical_clinic_system.service.PatientService;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** Direct-call tests: @PreAuthorize rules are not evaluated here. */
@ExtendWith(MockitoExtension.class)
class PatientGetControllerTest {

    @Mock
    private PatientService patientService;
    @Mock
    private AppointmentService appointmentService;

    private PatientGetController controller;

    @BeforeEach
    void setUp() {
        controller = new PatientGetController(patientService, appointmentService);
    }

    @Test
    void getPatientByID_delegatesToService() {
        UUID id = UUID.randomUUID();
        PatientResponse expected = mock(PatientResponse.class);
        when(patientService.findPatientByID(id)).thenReturn(expected);

        assertSame(expected, controller.getPatientByID(id));
    }

    // ---------- getPatients ----------

    @Test
    void getPatients_passesNameAndBuildsPageable() {
        when(patientService.findPatients(any(), any(Pageable.class))).thenReturn(Page.empty());

        controller.getPatients("ahmed", 3, 5, "name", "desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(patientService).findPatients(eq("ahmed"), captor.capture());
        assertEquals(3, captor.getValue().getPageNumber());
        assertEquals(5, captor.getValue().getPageSize());
        assertEquals(Sort.Direction.DESC, captor.getValue().getSort().getOrderFor("name").getDirection());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 11})
    void getPatients_invalidSize_throwsInvalidData(int size) {
        assertThrows(InvalidDataException.class, () -> controller.getPatients(null, 0, size, "name", "asc"));
        verifyNoInteractions(patientService);
    }

    @Test
    void getPatients_negativePage_throwsInvalidData() {
        assertThrows(InvalidDataException.class, () -> controller.getPatients(null, -1, 10, "name", "asc"));
        verifyNoInteractions(patientService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"password", "PASSWORD"})
    void getPatients_sortByPassword_isRejected(String sortBy) {
        assertThrows(InvalidDataException.class, () -> controller.getPatients(null, 0, 10, sortBy, "asc"));
        verifyNoInteractions(patientService);
    }

    // ---------- findPatientAppointments ----------

    @Test
    void findPatientAppointments_buildsPageableAndDelegates() {
        UUID id = UUID.randomUUID();
        when(appointmentService.findPatientAppointments(eq(id), any(Pageable.class))).thenReturn(Page.empty());

        controller.findPatientAppointments(id, 2, 10, "appointmentDateTime", "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(appointmentService).findPatientAppointments(eq(id), captor.capture());
        assertEquals(2, captor.getValue().getPageNumber());
        assertEquals(Sort.Direction.ASC, captor.getValue().getSort().getOrderFor("appointmentDateTime").getDirection());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 11})
    void findPatientAppointments_invalidSize_throwsInvalidData(int size) {
        UUID id = UUID.randomUUID();
        assertThrows(InvalidDataException.class,
                () -> controller.findPatientAppointments(id, 0, size, "appointmentDateTime", "asc"));
        verifyNoInteractions(appointmentService);
    }

    @Test
    void findPatientAppointments_negativePage_throwsInvalidData() {
        UUID id = UUID.randomUUID();
        assertThrows(InvalidDataException.class,
                () -> controller.findPatientAppointments(id, -1, 10, "appointmentDateTime", "asc"));
        verifyNoInteractions(appointmentService);
    }

    @Test
    void findPatientAppointments_pageAboveTen_isAllowed() {
        UUID id = UUID.randomUUID();
        when(appointmentService.findPatientAppointments(eq(id), any(Pageable.class))).thenReturn(Page.empty());

        assertDoesNotThrow(() -> controller.findPatientAppointments(id, 11, 10, "appointmentDateTime", "asc"));
    }

    @Test
    void findPatientAppointments_sortByPassword_isRejected() {
        UUID id = UUID.randomUUID();
        assertThrows(InvalidDataException.class,
                () -> controller.findPatientAppointments(id, 0, 10, "PASSWORD", "asc"));
        verifyNoInteractions(appointmentService);
    }
}
