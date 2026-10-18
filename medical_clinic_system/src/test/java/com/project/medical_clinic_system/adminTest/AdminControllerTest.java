package com.project.medical_clinic_system.adminTest;

import com.project.medical_clinic_system.controller.AdminController;
import com.project.medical_clinic_system.dto.request.CreateAdminRequest;
import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.service.AdminService;
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
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    private AdminController controller;

    @BeforeEach
    void setUp() {
        controller = new AdminController(adminService);
    }

    @Test
    void createAdmin_delegatesToService() {
        CreateAdminRequest request = mock(CreateAdminRequest.class);
        AdminResponse expected = mock(AdminResponse.class);
        when(adminService.createAdmin(request)).thenReturn(expected);

        assertSame(expected, controller.CreateAdmin(request));
    }

    @Test
    void updateAdmin_delegatesToService() {
        UUID id = UUID.randomUUID();
        CreateAdminRequest request = mock(CreateAdminRequest.class);
        AdminResponse expected = mock(AdminResponse.class);
        when(adminService.updateById(id, request)).thenReturn(expected);

        assertSame(expected, controller.updateAdmin(id, request));
    }

    @Test
    void deleteAdmin_returnsServiceMessage() {
        UUID id = UUID.randomUUID();
        when(adminService.deleteById(id)).thenReturn("Admin is deleted");

        assertEquals("Admin is deleted", controller.deleteAdmin(id));
    }
}
