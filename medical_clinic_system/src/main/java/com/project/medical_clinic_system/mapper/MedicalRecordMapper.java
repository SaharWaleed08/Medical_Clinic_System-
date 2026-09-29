package com.project.medical_clinic_system.mapper;


import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.model.MedicalVisitRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MedicalRecordMapper {
    public MedicalRecordResponse toResponse(MedicalVisitRecord medicalVisitRecord){
        return new MedicalRecordResponse(
                medicalVisitRecord.getId(),
                medicalVisitRecord.getPatientID(),
                medicalVisitRecord.getDiagnosis(),
                medicalVisitRecord.getMedicalNotes());
    }
    public List<MedicalRecordResponse> toResponse(List<MedicalVisitRecord> medicalVisitRecords){
        List<MedicalRecordResponse> responses = new ArrayList<>();

        for (MedicalVisitRecord medicalVisitRecord : medicalVisitRecords ) {
            responses.add(toResponse(medicalVisitRecord));
        }

        return responses;
    }
}
