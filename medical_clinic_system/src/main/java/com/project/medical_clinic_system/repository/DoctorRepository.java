package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Optional<Doctor> save(Doctor doctor);
    Optional<Doctor> findById(UUID doctorID);
    boolean existsById(UUID doctorID);
    void deleteById(UUID doctorID);
    List<Doctor> findAll();
}
