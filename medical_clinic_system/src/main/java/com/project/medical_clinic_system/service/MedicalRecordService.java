package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateMedicalRecordRequest;
import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.mapper.MedicalRecordMapper;
import com.project.medical_clinic_system.model.MedicalVisitRecord;
import com.project.medical_clinic_system.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalRecordMapper = medicalRecordMapper;
    }
    public MedicalRecordResponse CreateMedicalRecord(CreateMedicalRecordRequest request){
        MedicalVisitRecord medicalVisitRecord=new MedicalVisitRecord(request.getPatientID(),request.getDoctorID(),request.getAppointmentID(),request.getDiagnosis(),request.getMedicalNotes(),request.getRecordCreationDate());
        medicalRecordRepository.save(medicalVisitRecord);
        return medicalRecordMapper.toResponse(Optional.of(medicalVisitRecord));
    }
    public MedicalRecordResponse findById(UUID medicalRecordID){
        Optional<MedicalVisitRecord> medicalVisitRecord=medicalRecordRepository.findById(medicalRecordID);
        return medicalRecordMapper.toResponse(medicalVisitRecord);
    }
    public List<MedicalRecordResponse> findAll(){
        List<MedicalVisitRecord> medicalVisitRecords=medicalRecordRepository.findAll();
        return medicalRecordMapper.toResponse(medicalVisitRecords);
    }
    public String deleteMedicalRecordById(UUID medicalRecordID){
        medicalRecordRepository.deleteById(medicalRecordID);
        return "Medical Record is deleted";
    }
}
