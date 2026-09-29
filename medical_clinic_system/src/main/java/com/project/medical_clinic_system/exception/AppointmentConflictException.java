package com.project.medical_clinic_system.exception;

public class AppointmentConflictException extends RuntimeException{
    public AppointmentConflictException(String message){
        super(message);
    }
}
