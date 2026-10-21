package com.project.medical_clinic_system.exception;

public class ActiveSessionException extends RuntimeException {

    public ActiveSessionException(String message) {
        super(message);
    }
}