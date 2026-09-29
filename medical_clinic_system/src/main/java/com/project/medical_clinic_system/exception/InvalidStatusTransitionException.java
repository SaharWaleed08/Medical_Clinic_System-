package com.project.medical_clinic_system.exception;

public class InvalidStatusTransitionException extends RuntimeException{
    public InvalidStatusTransitionException(String message){
        super(message);
    }
}
