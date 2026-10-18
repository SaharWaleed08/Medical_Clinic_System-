package com.project.medical_clinic_system.availabilityTest;

import com.project.medical_clinic_system.controller.AvailabilityController;
import com.project.medical_clinic_system.controller.get.AvailabilityGetController;
import com.project.medical_clinic_system.dto.request.CreateAvailabilityRequest;
import com.project.medical_clinic_system.dto.response.AvailabilityResponse;
import com.project.medical_clinic_system.service.AvailabilityService;
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
class AvailabilityControllerTest {

    @Mock
    private AvailabilityService availabilityService;

    private AvailabilityController controller;
    private AvailabilityGetController getController;

    @BeforeEach
    void setUp() {
        controller = new AvailabilityController(availabilityService);
        getController = new AvailabilityGetController(availabilityService);
    }

    @Test
    void createAvailability_delegatesToService() {
        CreateAvailabilityRequest request = mock(CreateAvailabilityRequest.class);
        AvailabilityResponse expected = mock(AvailabilityResponse.class);
        when(availabilityService.createAvailability(request)).thenReturn(expected);

        assertSame(expected, controller.createAvailability(request));
    }

    @Test
    void deleteAvailability_returnsServiceMessage() {
        UUID id = UUID.randomUUID();
        when(availabilityService.deleteAvailabilityByID(id)).thenReturn("Availability is deleted");

        assertEquals("Availability is deleted", controller.deleteAvailability(id));
    }

    @Test
    void getAvailabilityByID_delegatesToService() {
        UUID id = UUID.randomUUID();
        AvailabilityResponse expected = mock(AvailabilityResponse.class);
        when(availabilityService.findAvailabilityByID(id)).thenReturn(expected);

        assertSame(expected, getController.getAvailabilityByID(id));
    }

    @Test
    void getAllAvailability_delegatesToService() {
        List<AvailabilityResponse> expected = List.of(mock(AvailabilityResponse.class));
        when(availabilityService.findAllAvailability()).thenReturn(expected);

        assertSame(expected, getController.getAllAvailability());
    }
}
