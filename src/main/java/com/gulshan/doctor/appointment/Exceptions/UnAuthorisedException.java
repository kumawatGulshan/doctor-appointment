package com.gulshan.doctor.appointment.Exceptions;

public class UnAuthorisedException extends RuntimeException {
    public UnAuthorisedException(String message) {
        super(message);
    }
}
