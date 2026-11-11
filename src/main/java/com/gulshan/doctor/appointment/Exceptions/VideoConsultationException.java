package com.gulshan.doctor.appointment.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VideoConsultationException extends RuntimeException {

    private final HttpStatus status;

    public VideoConsultationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}