package com.project.medical_clinic_system.mapper;

import com.project.medical_clinic_system.dto.response.DoctorResponse;
import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.model.Doctor;
import com.project.medical_clinic_system.model.MedicalVisitRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MedicalRecordMapper {
    public MedicalRecordResponse toResponse(Optional<MedicalVisitRecord> medicalVisitRecord){
        return new MedicalRecordResponse(medicalVisitRecord.get().getId(),medicalVisitRecord.get().getPatientID(),medicalVisitRecord.get().getDiagnosis(),medicalVisitRecord.get().getMedicalNotes());
    }
    public List<MedicalRecordResponse> toResponse(List<MedicalVisitRecord> medicalVisitRecords){
        List<MedicalRecordResponse> responses = new ArrayList<>();

        for (MedicalVisitRecord medicalVisitRecord : medicalVisitRecords ) {
            responses.add(toResponse(Optional.ofNullable(medicalVisitRecord)));
        }

        return responses;
    }
}
