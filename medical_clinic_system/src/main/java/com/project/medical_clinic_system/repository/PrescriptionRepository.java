package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    List<Prescription> findByMedicalVisitRecord_PatientID(UUID patientID);
    List<Prescription> findByMedicalVisitRecord_DoctorID(UUID doctorID);
}
