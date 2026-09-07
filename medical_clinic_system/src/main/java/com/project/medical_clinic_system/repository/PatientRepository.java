package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> save(Patient patient);
    Optional<Patient> findById(UUID patientID);
    boolean existsById(UUID patientID);
    void deleteById(UUID patientID);
    List<Patient> findAll();

}
