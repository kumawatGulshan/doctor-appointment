package com.gulshan.doctor.appointment.Exceptions;

public class InvalidApprovalStatusException extends RuntimeException {
    public InvalidApprovalStatusException(String message) {
        super(message);
    }
}
