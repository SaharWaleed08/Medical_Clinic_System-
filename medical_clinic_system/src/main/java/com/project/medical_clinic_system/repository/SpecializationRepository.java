package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.dto.request.CreateSpecializationRequest;
import com.project.medical_clinic_system.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {
   Optional<Specialization> updateById(UUID specializationID, CreateSpecializationRequest request);
}
