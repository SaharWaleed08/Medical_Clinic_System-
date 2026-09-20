package com.project.medical_clinic_system.exception;

public class DoctorUnavailable extends RuntimeException{
    public DoctorUnavailable(String message){
        super(message);
    }
}
