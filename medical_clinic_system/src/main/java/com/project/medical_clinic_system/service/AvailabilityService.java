package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateAvailabilityRequest;
import com.project.medical_clinic_system.dto.response.AvailabilityResponse;
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

        return new AvailabilityResponse(
                availability.getId(),
                availability.getDoctor().getDoctorID(),
                availability.getDoctor().getName(),
                availability.getDay(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }

    public AvailabilityResponse findAvailabilityByID(UUID id) {
        Optional<Availability> availability = availabilityRepository.findById(id);
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