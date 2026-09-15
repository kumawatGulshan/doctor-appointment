package com.gulshan.doctor.appointment.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@AllArgsConstructor
public class ApiError {

    private String message;
    private HttpStatus status;
    private List<String> subMessage;
}
