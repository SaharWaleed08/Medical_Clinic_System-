package com.project.medical_clinic_system.repository;
import com.project.medical_clinic_system.dto.request.CreateDoctorRequest;
import com.project.medical_clinic_system.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Optional<Doctor> updateById(UUID doctorID, CreateDoctorRequest request);
}
