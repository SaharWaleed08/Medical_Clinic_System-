package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateAvailabilityRequest;
import com.project.medical_clinic_system.dto.response.AvailabilityResponse;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.AvailabilityMapper;
import com.project.medical_clinic_system.model.Availability;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.repository.AvailabilityRepository;
import com.project.medical_clinic_system.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityMapper availabilityMapper;
    private final DoctorRepository doctorRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository,
                               AvailabilityMapper availabilityMapper,
                               DoctorRepository doctorRepository) {
        this.availabilityRepository = availabilityRepository;
        this.availabilityMapper = availabilityMapper;
        this.doctorRepository = doctorRepository;
    }

    public AvailabilityResponse createAvailability(CreateAvailabilityRequest request) {
        Optional<Doctor> doctor = doctorRepository.findById(request.getDoctorID());

        Availability availability = new Availability(
                doctor.get(),
                request.getDay(),
                request.getStartTime(),
                request.getEndTime()
        );

        availabilityRepository.save(availability);

        return availabilityMapper.toResponse(availability);
    }

    public AvailabilityResponse findAvailabilityByID(UUID id) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));
        return availabilityMapper.toResponse(availability);
    }

    public List<AvailabilityResponse> findAllAvailability() {
        List<Availability> availabilities = availabilityRepository.findAll();
        return availabilityMapper.toResponse(availabilities);
    }

    public String deleteAvailabilityByID(UUID id) {
        availabilityRepository.deleteById(id);
        return "Availability is deleted";
    }
}