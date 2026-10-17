package com.project.medical_clinic_system.service;

import com.project.medical_clinic_system.dto.request.CreateMedicalRecordRequest;
import com.project.medical_clinic_system.dto.response.MedicalRecordResponse;
import com.project.medical_clinic_system.exception.ResourceNotFoundException;
import com.project.medical_clinic_system.mapper.MedicalRecordMapper;
import com.project.medical_clinic_system.model.MedicalVisitRecord;
import com.project.medical_clinic_system.repository.MedicalRecordRepository;
import com.project.medical_clinic_system.model.Appointment;
import com.project.medical_clinic_system.repository.AppointmentRepository;
import org.springframework.security.access.AccessDeniedException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, MedicalRecordMapper medicalRecordMapper, AppointmentRepository appointmentRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalRecordMapper = medicalRecordMapper;
        this.appointmentRepository = appointmentRepository;
    }
    public MedicalRecordResponse CreateMedicalRecord(CreateMedicalRecordRequest request, UUID doctorID) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentID())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(doctorID)) {
            throw new AccessDeniedException("This appointment does not belong to you");
        }

        MedicalVisitRecord medicalVisitRecord = new MedicalVisitRecord(
                appointment.getPatient().getId(),
                doctorID,
                appointment.getId(),
                request.getDiagnosis(),
                request.getMedicalNotes(),
                request.getRecordCreationDate()
        );

        medicalRecordRepository.save(medicalVisitRecord);
        return medicalRecordMapper.toResponse(medicalVisitRecord);
    }
    public MedicalRecordResponse findById(UUID medicalRecordID){
        MedicalVisitRecord medicalVisitRecord=medicalRecordRepository.findById(medicalRecordID)
                .orElseThrow(()->new ResourceNotFoundException("Medical visit record not found"));
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
