package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateSpecializationRequest;
import com.project.medical_clinic_system.dto.response.SpecializationResponse;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.SpecializationMapper;
import com.project.medical_clinic_system.model.Specialization;
import com.project.medical_clinic_system.repository.SpecializationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
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
        Specialization specialization = specializationRepository.findById(specializationID)
                .orElseThrow(()->new ResourceNotFoundException("Specialization not found"));
        return specializationMapper.toResponse(specialization);
    }

    public List<SpecializationResponse> findAllSpecialization() {
        List<Specialization> specializations = specializationRepository.findAll();
        return specializationMapper.toResponse(specializations);
    }

    public SpecializationResponse updateSpecializationByID(UUID specializationID, CreateSpecializationRequest request) {
        Specialization specialization = specializationRepository.findById(specializationID)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));

        specialization.setName(request.getName());
        specialization.setDescription(request.getDescription());

        specializationRepository.save(specialization);

        return specializationMapper.toResponse(specialization);
    }

    public String deleteSpecializationByID(UUID specializationID) {
        specializationRepository.deleteById(specializationID);
        return "Specialization is deleted";
    }
}
