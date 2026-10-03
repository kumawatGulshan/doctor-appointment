package com.gulshan.doctor.appointment.Exceptions;

public class InvalidAppointmentException extends RuntimeException {
    public InvalidAppointmentException(String message) {
        super(message);
    }
}
