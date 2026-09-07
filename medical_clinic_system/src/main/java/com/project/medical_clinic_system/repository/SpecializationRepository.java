package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.model.Patient;
import com.project.medical_clinic_system.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {
    Optional<Specialization> save(Specialization specialization);
    Optional<Specialization> findById(UUID specializationID);
    boolean existsById(UUID specializationID);
    void deleteById(UUID specializationID);
    List<Specialization> findAll();
}
