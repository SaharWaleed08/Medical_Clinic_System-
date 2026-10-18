package com.project.medical_clinic_system.appointmentTest;

import com.project.medical_clinic_system.dto.request.CreateAppointmentRequest;
import com.project.medical_clinic_system.dto.response.AppointmentResponse;
import com.project.medical_clinic_system.dto.response.AvailableSlotResponse;
import com.project.medical_clinic_system.enums.AppointmentStatus;
import com.project.medical_clinic_system.enums.Gender;
import com.project.medical_clinic_system.exception.*;
import com.project.medical_clinic_system.mapper.AppointmentMapper;
import com.project.medical_clinic_system.model.Appointment;
import com.project.medical_clinic_system.model.Availability;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.repository.AppointmentRepository;
import com.project.medical_clinic_system.repository.AvailabilityRepository;
import com.project.medical_clinic_system.repository.DoctorRepository;
import com.project.medical_clinic_system.repository.PatientRepository;
import com.project.medical_clinic_system.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import com.project.medical_clinic_system.model.Specialization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private AvailabilityRepository availabilityRepository;
    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    private final Pageable pageable = PageRequest.of(0, 10);

    private UUID patientId;
    private UUID doctorId;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime futureSlot;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();

        patient = new Patient("Patient Ahmed Ali", "p@mail.com", "pw", "01012345678",
                LocalDate.of(1995, 1, 1), Gender.MALE, LocalDateTime.of(2026, 1, 1, 0, 0));
        patient.setId(patientId);

        doctor = new Doctor("Doctor Mona Samir", "d@mail.com", "pw", "01112345678", new Specialization("Cardiology", "Heart diseases"), 
                "1234-5678", 10, new BigDecimal("300"));
        doctor.setId(doctorId);

        // tomorrow at 10:00 -> always in the future
        futureSlot = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    // ---------- helpers ----------

    private CreateAppointmentRequest request(LocalDateTime dateTime) {
        CreateAppointmentRequest r = new CreateAppointmentRequest();
        r.setPatientID(patientId);
        r.setDoctorID(doctorId);
        r.setAppointmentDateTime(dateTime);
        r.setReasonForVisit("Checkup");
        return r;
    }

    private Availability availability(LocalDateTime forDay, int fromHour, int toHour) {
        return new Availability(doctor, forDay.getDayOfWeek(), LocalTime.of(fromHour, 0), LocalTime.of(toHour, 0));
    }

    private Appointment appointment(AppointmentStatus status) {
        Appointment a = new Appointment(patient, doctor, futureSlot, "Checkup");
        a.setId(UUID.randomUUID());
        a.setStatus(status);
        return a;
    }

    private AppointmentResponse response(Appointment a) {
        return new AppointmentResponse(a.getId(), patientId, patient.getName(), doctorId, doctor.getName(),
                a.getAppointmentDateTime(), a.getReasonForVisit(), a.getStatus());
    }

    private void stubPatientAndDoctorFound() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
    }

    // =====================================================================
    // createAppointment
    // =====================================================================

    @Test
    void createAppointment_success_savesPendingAppointment() {
        CreateAppointmentRequest request = request(futureSlot);
        stubPatientAndDoctorFound();
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                doctorId, futureSlot, AppointmentStatus.CANCELLED)).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndAppointmentDateTimeAndStatusNot(
                patientId, futureSlot, AppointmentStatus.CANCELLED)).thenReturn(false);
        AppointmentResponse expected = response(appointment(AppointmentStatus.PENDING));
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(expected);

        AppointmentResponse result = appointmentService.createAppointment(request);

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(captor.capture());
        Appointment saved = captor.getValue();

        assertSame(expected, result);
        assertEquals(AppointmentStatus.PENDING, saved.getStatus());
        assertSame(patient, saved.getPatient());
        assertSame(doctor, saved.getDoctor());
        assertEquals(futureSlot, saved.getAppointmentDateTime());
        assertEquals("Checkup", saved.getReasonForVisit());
    }

    @Test
    void createAppointment_patientNotFound_throws() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.createAppointment(request(futureSlot)));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_doctorNotFound_throws() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.createAppointment(request(futureSlot)));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_doctorAlreadyBooked_throwsConflict() {
        stubPatientAndDoctorFound();
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                doctorId, futureSlot, AppointmentStatus.CANCELLED)).thenReturn(true);

        AppointmentConflictException ex = assertThrows(AppointmentConflictException.class,
                () -> appointmentService.createAppointment(request(futureSlot)));

        assertEquals("Doctor already has an appointment at this time", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_patientAlreadyBooked_throwsConflict() {
        stubPatientAndDoctorFound();
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                doctorId, futureSlot, AppointmentStatus.CANCELLED)).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndAppointmentDateTimeAndStatusNot(
                patientId, futureSlot, AppointmentStatus.CANCELLED)).thenReturn(true);

        AppointmentConflictException ex = assertThrows(AppointmentConflictException.class,
                () -> appointmentService.createAppointment(request(futureSlot)));

        assertEquals("Patient already has an appointment at this time", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void createAppointment_conflictChecksIgnoreCancelledAppointments() {
        // a cancelled appointment must not block re-booking the same slot
        stubPatientAndDoctorFound();
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsByPatientIdAndAppointmentDateTimeAndStatusNot(
                any(), any(), any())).thenReturn(false);
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(mock(AppointmentResponse.class));

        appointmentService.createAppointment(request(futureSlot));

        verify(appointmentRepository).existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                doctorId, futureSlot, AppointmentStatus.CANCELLED);
        verify(appointmentRepository).existsByPatientIdAndAppointmentDateTimeAndStatusNot(
                patientId, futureSlot, AppointmentStatus.CANCELLED);
    }

    // =====================================================================
    // find*
    // =====================================================================

    @Test
    void findAppointmentByID_found_returnsResponse() {
        Appointment a = appointment(AppointmentStatus.PENDING);
        AppointmentResponse expected = response(a);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));
        when(appointmentMapper.toResponse(a)).thenReturn(expected);

        assertSame(expected, appointmentService.findAppointmentByID(a.getId()));
    }

    @Test
    void findAppointmentByID_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.findAppointmentByID(id));
    }

    @Test
    void findPatientAppointments_patientNotFound_throws() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.findPatientAppointments(patientId, pageable));
        verify(appointmentRepository, never()).findByPatientId(any(), any());
    }

    @Test
    void findPatientAppointments_success_returnsMappedPage() {
        Appointment a = appointment(AppointmentStatus.PENDING);
        AppointmentResponse expected = response(a);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatientId(patientId, pageable))
                .thenReturn(new PageImpl<>(List.of(a), pageable, 1));
        when(appointmentMapper.toResponse(a)).thenReturn(expected);

        Page<AppointmentResponse> result = appointmentService.findPatientAppointments(patientId, pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(expected, result.getContent().get(0));
    }

    @Test
    void findDoctorAppointments_doctorNotFound_throws() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.findDoctorAppointments(doctorId, pageable));
        verify(appointmentRepository, never()).findByDoctorId(any(), any());
    }

    @Test
    void findDoctorAppointments_success_returnsMappedPage() {
        Appointment a = appointment(AppointmentStatus.CONFIRMED);
        AppointmentResponse expected = response(a);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorId(doctorId, pageable))
                .thenReturn(new PageImpl<>(List.of(a), pageable, 1));
        when(appointmentMapper.toResponse(a)).thenReturn(expected);

        Page<AppointmentResponse> result = appointmentService.findDoctorAppointments(doctorId, pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(expected, result.getContent().get(0));
    }

    // ---------- findAppointments (optional status + date-range filters) ----------

    private final LocalDateTime rangeStart = LocalDateTime.of(2030, 1, 1, 0, 0);
    private final LocalDateTime rangeEnd = LocalDateTime.of(2030, 1, 31, 23, 59);

    private Appointment mappedAppointment() {
        Appointment a = appointment(AppointmentStatus.CANCELLED);
        when(appointmentMapper.toResponse(a)).thenReturn(response(a));
        return a;
    }

    @Test
    void findAppointments_noFilters_usesFindAll() {
        Appointment a = mappedAppointment();
        when(appointmentRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(a), pageable, 1));

        Page<AppointmentResponse> result = appointmentService.findAppointments(null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository, never()).findByStatus(any(), any());
        verify(appointmentRepository, never()).findByAppointmentDateTimeBetween(any(), any(), any());
    }

    @Test
    void findAppointments_onlyStatus_usesStatusQuery() {
        Appointment a = mappedAppointment();
        when(appointmentRepository.findByStatus(AppointmentStatus.CANCELLED, pageable))
                .thenReturn(new PageImpl<>(List.of(a), pageable, 1));

        Page<AppointmentResponse> result =
                appointmentService.findAppointments(AppointmentStatus.CANCELLED, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void findAppointments_onlyDateRange_usesDateQuery() {
        Appointment a = mappedAppointment();
        when(appointmentRepository.findByAppointmentDateTimeBetween(rangeStart, rangeEnd, pageable))
                .thenReturn(new PageImpl<>(List.of(a), pageable, 1));

        Page<AppointmentResponse> result =
                appointmentService.findAppointments(null, rangeStart, rangeEnd, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void findAppointments_statusAndDateRange_usesCombinedQuery() {
        Appointment a = mappedAppointment();
        when(appointmentRepository.findByStatusAndAppointmentDateTimeBetween(
                AppointmentStatus.PENDING, rangeStart, rangeEnd, pageable))
                .thenReturn(new PageImpl<>(List.of(a), pageable, 1));

        Page<AppointmentResponse> result =
                appointmentService.findAppointments(AppointmentStatus.PENDING, rangeStart, rangeEnd, pageable);

        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository, never()).findByStatus(any(), any());
        verify(appointmentRepository, never()).findByAppointmentDateTimeBetween(any(), any(), any());
    }

    @Test
    void findAppointments_onlyStartWithoutEnd_ignoresDateFilter() {
        when(appointmentRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        assertTrue(appointmentService.findAppointments(null, rangeStart, null, pageable).isEmpty());
        verify(appointmentRepository, never()).findByAppointmentDateTimeBetween(any(), any(), any());
    }

    @Test
    void findAppointments_statusWithOnlyEnd_fallsBackToStatusQuery() {
        when(appointmentRepository.findByStatus(AppointmentStatus.PENDING, pageable)).thenReturn(Page.empty(pageable));

        assertTrue(appointmentService.findAppointments(AppointmentStatus.PENDING, null, rangeEnd, pageable).isEmpty());
        verify(appointmentRepository, never()).findByStatusAndAppointmentDateTimeBetween(any(), any(), any(), any());
    }

    // =====================================================================
    // confirm
    // =====================================================================

    @Test
    void confirmAppointment_pending_becomesConfirmed() {
        Appointment a = appointment(AppointmentStatus.PENDING);
        AppointmentResponse expected = response(a);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));
        when(appointmentMapper.toResponse(a)).thenReturn(expected);

        AppointmentResponse result = appointmentService.confirmAppointment(a.getId());

        assertSame(expected, result);
        assertEquals(AppointmentStatus.CONFIRMED, a.getStatus());
        verify(appointmentRepository).save(a);
    }

    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, names = "PENDING", mode = EnumSource.Mode.EXCLUDE)
    void confirmAppointment_notPending_throwsInvalidTransition(AppointmentStatus status) {
        Appointment a = appointment(status);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));

        assertThrows(InvalidStatusTransitionException.class, () -> appointmentService.confirmAppointment(a.getId()));

        assertEquals(status, a.getStatus());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void confirmAppointment_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.confirmAppointment(id));
    }

    // =====================================================================
    // cancel
    // =====================================================================

    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, names = {"PENDING", "CONFIRMED"})
    void cancelAppointment_pendingOrConfirmed_becomesCancelled(AppointmentStatus status) {
        Appointment a = appointment(status);
        AppointmentResponse expected = response(a);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));
        when(appointmentMapper.toResponse(a)).thenReturn(expected);

        AppointmentResponse result = appointmentService.cancelAppointment(a.getId());

        assertSame(expected, result);
        assertEquals(AppointmentStatus.CANCELLED, a.getStatus());
        verify(appointmentRepository).save(a);
    }

    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, names = {"CANCELLED", "COMPLETED", "NO_SHOW"})
    void cancelAppointment_finalStatus_throwsInvalidTransition(AppointmentStatus status) {
        Appointment a = appointment(status);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));

        assertThrows(InvalidStatusTransitionException.class, () -> appointmentService.cancelAppointment(a.getId()));

        assertEquals(status, a.getStatus());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancelAppointment_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.cancelAppointment(id));
    }

    // =====================================================================
    // complete
    // =====================================================================

    @Test
    void completeAppointment_confirmed_becomesCompleted() {
        Appointment a = appointment(AppointmentStatus.CONFIRMED);
        AppointmentResponse expected = response(a);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));
        when(appointmentMapper.toResponse(a)).thenReturn(expected);

        AppointmentResponse result = appointmentService.completeAppointment(a.getId());

        assertSame(expected, result);
        assertEquals(AppointmentStatus.COMPLETED, a.getStatus());
        verify(appointmentRepository).save(a);
    }

    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, names = "CONFIRMED", mode = EnumSource.Mode.EXCLUDE)
    void completeAppointment_notConfirmed_throwsInvalidTransition(AppointmentStatus status) {
        Appointment a = appointment(status);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));

        assertThrows(InvalidStatusTransitionException.class, () -> appointmentService.completeAppointment(a.getId()));

        assertEquals(status, a.getStatus());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void completeAppointment_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.completeAppointment(id));
    }

    // =====================================================================
    // no-show
    // =====================================================================

    @Test
    void markAppointmentAsNoShow_confirmed_becomesNoShow() {
        Appointment a = appointment(AppointmentStatus.CONFIRMED);
        AppointmentResponse expected = response(a);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));
        when(appointmentMapper.toResponse(a)).thenReturn(expected);

        AppointmentResponse result = appointmentService.markAppointmentAsNoShow(a.getId());

        assertSame(expected, result);
        assertEquals(AppointmentStatus.NO_SHOW, a.getStatus());
        verify(appointmentRepository).save(a);
    }

    @ParameterizedTest
    @EnumSource(value = AppointmentStatus.class, names = "CONFIRMED", mode = EnumSource.Mode.EXCLUDE)
    void markAppointmentAsNoShow_notConfirmed_throwsInvalidTransition(AppointmentStatus status) {
        Appointment a = appointment(status);
        when(appointmentRepository.findById(a.getId())).thenReturn(Optional.of(a));

        assertThrows(InvalidStatusTransitionException.class,
                () -> appointmentService.markAppointmentAsNoShow(a.getId()));

        assertEquals(status, a.getStatus());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void markAppointmentAsNoShow_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.markAppointmentAsNoShow(id));
    }

    // =====================================================================
    // getAvailableSlots
    // =====================================================================

    // 2030-01-07 is a Monday
    private final LocalDate monday = LocalDate.of(2030, 1, 7);

    @Test
    void getAvailableSlots_doctorNotFound_throws() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.getAvailableSlots(doctorId, monday));
    }

    @Test
    void getAvailableSlots_noAvailabilityThatDay_returnsEmptyList() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(availabilityRepository.findByDoctorIdAndDay(doctorId, DayOfWeek.MONDAY)).thenReturn(List.of());

        assertTrue(appointmentService.getAvailableSlots(doctorId, monday).isEmpty());
    }

    @Test
    void getAvailableSlots_generatesHalfHourSlotsExcludingEndTime() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(availabilityRepository.findByDoctorIdAndDay(doctorId, DayOfWeek.MONDAY))
                .thenReturn(List.of(new Availability(doctor, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0))));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                eq(doctorId), any(LocalDateTime.class), eq(AppointmentStatus.CANCELLED))).thenReturn(false);

        List<AvailableSlotResponse> slots = appointmentService.getAvailableSlots(doctorId, monday);

        assertEquals(4, slots.size());
        assertEquals(monday.atTime(9, 0), slots.get(0).getStartTime());
        assertEquals(monday.atTime(9, 30), slots.get(1).getStartTime());
        assertEquals(monday.atTime(10, 0), slots.get(2).getStartTime());
        assertEquals(monday.atTime(10, 30), slots.get(3).getStartTime());
    }

    @Test
    void getAvailableSlots_skipsAlreadyBookedSlots() {
        LocalDateTime booked = monday.atTime(9, 30);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(availabilityRepository.findByDoctorIdAndDay(doctorId, DayOfWeek.MONDAY))
                .thenReturn(List.of(new Availability(doctor, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0))));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                eq(doctorId), any(LocalDateTime.class), eq(AppointmentStatus.CANCELLED)))
                .thenAnswer(inv -> booked.equals(inv.getArgument(1)));

        List<AvailableSlotResponse> slots = appointmentService.getAvailableSlots(doctorId, monday);

        assertEquals(3, slots.size());
        assertTrue(slots.stream().noneMatch(s -> s.getStartTime().equals(booked)));
    }

    @Test
    void getAvailableSlots_multipleAvailabilityWindows_combinesAllSlots() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(availabilityRepository.findByDoctorIdAndDay(doctorId, DayOfWeek.MONDAY)).thenReturn(List.of(
                new Availability(doctor, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)),
                new Availability(doctor, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 0))));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
                eq(doctorId), any(LocalDateTime.class), eq(AppointmentStatus.CANCELLED))).thenReturn(false);

        List<AvailableSlotResponse> slots = appointmentService.getAvailableSlots(doctorId, monday);

        assertEquals(4, slots.size());
        assertEquals(monday.atTime(14, 30), slots.get(3).getStartTime());
    }
}
