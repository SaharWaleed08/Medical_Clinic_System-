package com.project.medical_clinic_system.adminTest;

import com.project.medical_clinic_system.dto.request.CreateAdminRequest;
import com.project.medical_clinic_system.dto.response.AdminResponse;
import com.project.medical_clinic_system.enums.Role;
import com.project.medical_clinic_system.exception.DuplicateResourceException;
import com.project.medical_clinic_system.exception.InvalidAdminRegistrationPasswordException;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.AdminMapper;
import com.project.medical_clinic_system.model.Admin;
import com.project.medical_clinic_system.repository.AdminRepository;
import com.project.medical_clinic_system.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    private static final String ADMIN_SECRET = "super-secret";

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private AdminMapper adminMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    private final Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        // adminPassword is injected with @Value in production; set it manually here
        ReflectionTestUtils.setField(adminService, "adminPassword", ADMIN_SECRET);
    }

    private CreateAdminRequest request(String adminPassword) {
        return new CreateAdminRequest("Admin Ahmed Hassan", "Passw0rd@1", "admin@mail.com",
                "01012345678", adminPassword);
    }

    private Admin admin(UUID id) {
        Admin a = new Admin("Admin Ahmed Hassan", "admin@mail.com", "encoded", "01012345678");
        a.setId(id);
        return a;
    }

    // ---------- createAdmin ----------

    @Test
    void createAdmin_success_savesAdminWithEncodedPassword() {
        CreateAdminRequest request = request(ADMIN_SECRET);
        AdminResponse expected = new AdminResponse(UUID.randomUUID(), request.getName(), request.getEmail());

        when(adminRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(adminMapper.toResponse(any(Admin.class))).thenReturn(expected);

        AdminResponse result = adminService.createAdmin(request);

        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).save(captor.capture());

        assertSame(expected, result);
        assertEquals("encoded-pass", captor.getValue().getPassword());
        assertEquals(Role.ADMIN, captor.getValue().getRole());
        assertEquals(request.getEmail(), captor.getValue().getEmail());
    }

    @Test
    void createAdmin_duplicateEmail_throwsAndDoesNotSave() {
        CreateAdminRequest request = request(ADMIN_SECRET);
        when(adminRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> adminService.createAdmin(request));

        verify(adminRepository, never()).save(any());
    }

    @Test
    void createAdmin_wrongAdminPassword_throwsAndDoesNotSave() {
        CreateAdminRequest request = request("wrong-secret");
        when(adminRepository.existsByEmail(request.getEmail())).thenReturn(false);

        assertThrows(InvalidAdminRegistrationPasswordException.class, () -> adminService.createAdmin(request));

        verify(adminRepository, never()).save(any());
    }

    @Test
    void createAdmin_nullAdminPassword_throwsInvalidAdminPassword() {
        CreateAdminRequest request = request(null);
        when(adminRepository.existsByEmail(request.getEmail())).thenReturn(false);

        assertThrows(InvalidAdminRegistrationPasswordException.class, () -> adminService.createAdmin(request));
    }

    // ---------- findById ----------

    @Test
    void findById_found_returnsResponse() {
        UUID id = UUID.randomUUID();
        Admin a = admin(id);
        AdminResponse expected = new AdminResponse(id, a.getName(), a.getEmail());
        when(adminRepository.findById(id)).thenReturn(Optional.of(a));
        when(adminMapper.toResponse(a)).thenReturn(expected);

        assertSame(expected, adminService.findById(id));
    }

    @Test
    void findById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(adminRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.findById(id));
    }

    // ---------- findAll ----------

    @Test
    void findAll_nullName_usesFindAll() {
        UUID id = UUID.randomUUID();
        when(adminRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(admin(id)), pageable, 1));

        Page<AdminResponse> result = adminService.findAll(null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(id, result.getContent().get(0).getId());
        verify(adminRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void findAll_blankName_usesFindAll() {
        when(adminRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        assertTrue(adminService.findAll("", pageable).isEmpty());
        verify(adminRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void findAll_withName_usesSearchQuery() {
        when(adminRepository.findByNameContainingIgnoreCase("admin", pageable))
                .thenReturn(new PageImpl<>(List.of(admin(UUID.randomUUID())), pageable, 1));

        Page<AdminResponse> result = adminService.findAll("admin", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("admin@mail.com", result.getContent().get(0).getEmail());
        verify(adminRepository, never()).findAll(any(Pageable.class));
    }

    // ---------- updateById ----------

    @Test
    void updateById_found_updatesAllFields() {
        UUID id = UUID.randomUUID();
        Admin existing = admin(id);
        CreateAdminRequest request = new CreateAdminRequest("Admin Mona Samir Ali", "NewPass@123",
                "new@mail.com", "01198765432", ADMIN_SECRET);
        AdminResponse expected = new AdminResponse(id, request.getName(), request.getEmail());

        when(adminRepository.findById(id)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("NewPass@123")).thenReturn("new-encoded");
        when(adminMapper.toResponse(existing)).thenReturn(expected);

        AdminResponse result = adminService.updateById(id, request);

        assertSame(expected, result);
        assertEquals("Admin Mona Samir Ali", existing.getName());
        assertEquals("new@mail.com", existing.getEmail());
        assertEquals("new-encoded", existing.getPassword());
        assertEquals("01198765432", existing.getPhone());
    }

    @Test
    void updateById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(adminRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.updateById(id, request(ADMIN_SECRET)));
        verify(passwordEncoder, never()).encode(any());
    }

    // ---------- deleteById ----------

    @Test
    void deleteById_deletesAndReturnsMessage() {
        UUID id = UUID.randomUUID();

        assertEquals("Admin is deleted", adminService.deleteById(id));
        verify(adminRepository).deleteById(id);
    }
}
