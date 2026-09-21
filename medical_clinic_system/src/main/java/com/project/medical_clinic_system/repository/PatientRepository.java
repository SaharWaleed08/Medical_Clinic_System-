package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Page<Patient> findByNameContainingIgnoreCase(String name, Pageable pageable);
}