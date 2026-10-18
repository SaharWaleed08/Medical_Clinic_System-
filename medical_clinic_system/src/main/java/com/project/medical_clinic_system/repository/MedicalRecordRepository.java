package com.project.medical_clinic_system.repository;

import com.project.medical_clinic_system.model.MedicalVisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalVisitRecord, UUID> {
    List<MedicalVisitRecord> findByPatientID(UUID patientID);
    List<MedicalVisitRecord> findByDoctorID(UUID doctorID);
}
