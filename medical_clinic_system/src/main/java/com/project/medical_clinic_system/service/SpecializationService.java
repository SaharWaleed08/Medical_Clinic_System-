package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateSpecializationRequest;
import com.project.medical_clinic_system.dto.response.SpecializationResponse;
import com.project.medical_clinic_system.mapper.SpecializationMapper;
import com.project.medical_clinic_system.model.Specialization;
import com.project.medical_clinic_system.repository.SpecializationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SpecializationService {
    private final SpecializationRepository specializationRepository;
    private final SpecializationMapper specializationMapper;

    public SpecializationService(SpecializationRepository specializationRepository, SpecializationMapper specializationMapper) {
        this.specializationRepository = specializationRepository;
        this.specializationMapper = specializationMapper;
    }

    public SpecializationResponse createSpecialization(CreateSpecializationRequest request) {
        Specialization specialization = new Specialization(request.getName(), request.getDescription());
        specializationRepository.save(specialization);
        return new SpecializationResponse(specialization.getSpecializationID(), specialization.getName(), specialization.getDescription());
    }

    public SpecializationResponse findSpecializationByID(UUID specializationID) {
        Optional<Specialization> specialization = specializationRepository.findById(specializationID);
        return specializationMapper.toResponse(specialization);
    }

    public List<SpecializationResponse> findAllSpecialization() {
        List<Specialization> specializations = specializationRepository.findAll();
        return specializationMapper.toResponse(specializations);
    }

    public SpecializationResponse updateSpecializationByID(UUID specializationID, CreateSpecializationRequest request) {

        boolean existingSpecialization = specializationRepository.existsById(specializationID);

        if (existingSpecialization) {

            Optional<Specialization> specialization = specializationRepository.findById(specializationID);

            if (specialization.isPresent()) {

                Specialization existingSpecializationData = specialization.get();

                existingSpecializationData.setName(request.getName());
                existingSpecializationData.setDescription(request.getDescription());


                Optional<Specialization> updatedSpecialization = specializationRepository.save(existingSpecializationData);

                return specializationMapper.toResponse(updatedSpecialization);
            }
        }

        throw new RuntimeException("Specialization not found");
    }

    public String deleteSpecializationByID(UUID specializationID) {
        specializationRepository.deleteById(specializationID);
        return "Specialization is deleted";
    }
}
