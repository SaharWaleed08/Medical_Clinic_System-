package com.project.medical_clinic_system.adminTest;

import com.project.medical_clinic_system.controller.get.AdminGetController;
import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.exception.InvalidDataException;
import com.project.medical_clinic_system.service.AdminService;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Calls the controller methods directly (no Spring context), so @PreAuthorize is NOT applied here —
 * these tests cover the input validation and Pageable-building logic inside the controller.
 */
@ExtendWith(MockitoExtension.class)
class AdminGetControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminGetController controller;

    private Pageable capturePageable(String name) {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(adminService).findAll(eq(name), captor.capture());
        return captor.getValue();
    }

    @Test
    void getAdmin_delegatesToService() {
        UUID id = UUID.randomUUID();
        AdminResponse expected = mock(AdminResponse.class);
        when(adminService.findById(id)).thenReturn(expected);

        assertSame(expected, controller.getAdmin(id));
    }

    @Test
    void getAdmins_defaults_buildsPageableSortedByNameAscending() {
        when(adminService.findAll(any(), any(Pageable.class))).thenReturn(Page.empty());

        controller.getAdmins(null, 0, 10, "name", "asc");

        Pageable pageable = capturePageable(null);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("name").getDirection());
    }

    @Test
    void getAdmins_passesNameFilterToService() {
        when(adminService.findAll(any(), any(Pageable.class))).thenReturn(Page.empty());

        controller.getAdmins("root", 0, 10, "name", "asc");

        capturePageable("root");
    }

    @Test
    void getAdmins_directionDesc_sortsDescending() {
        when(adminService.findAll(any(), any(Pageable.class))).thenReturn(Page.empty());

        controller.getAdmins(null, 2, 5, "email", "desc");

        Pageable pageable = capturePageable(null);
        assertEquals(2, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("email").getDirection());
    }

    @Test
    void getAdmins_directionIsCaseInsensitive() {
        when(adminService.findAll(any(), any(Pageable.class))).thenReturn(Page.empty());

        controller.getAdmins(null, 0, 10, "name", "ASC");

        assertEquals(Sort.Direction.ASC, capturePageable(null).getSort().getOrderFor("name").getDirection());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void getAdmins_sizeBoundaries_areAllowed(int size) {
        when(adminService.findAll(any(), any(Pageable.class))).thenReturn(Page.empty());

        assertDoesNotThrow(() -> controller.getAdmins(null, 0, size, "name", "asc"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 11, 100})
    void getAdmins_invalidSize_throwsInvalidData(int size) {
        assertThrows(InvalidDataException.class, () -> controller.getAdmins(null, 0, size, "name", "asc"));
        verifyNoInteractions(adminService);
    }

    @Test
    void getAdmins_negativePage_throwsInvalidData() {
        assertThrows(InvalidDataException.class, () -> controller.getAdmins(null, -1, 10, "name", "asc"));
        verifyNoInteractions(adminService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"password", "PASSWORD", "PassWord"})
    void getAdmins_sortByPassword_isRejected(String sortBy) {
        assertThrows(InvalidDataException.class, () -> controller.getAdmins(null, 0, 10, sortBy, "asc"));
        verifyNoInteractions(adminService);
    }
}
