package com.gulshan.doctor.appointment.advice;

import com.gulshan.doctor.appointment.Exceptions.*;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex){
        ApiError apiError = new ApiError(ex.getLocalizedMessage(), HttpStatus.UNAUTHORIZED,null);
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException ex){
        ApiError apiError = new ApiError(ex.getLocalizedMessage(),HttpStatus.UNAUTHORIZED,null);
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = new ApiError(ex.getLocalizedMessage(), HttpStatus.FORBIDDEN,null);
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleAuthenticationHandler(ResourceNotFoundException ex){
        ApiError apiError = new ApiError(ex.getLocalizedMessage(),HttpStatus.NOT_FOUND,null);
        return new ResponseEntity<>(apiError,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResourceException(DuplicateResourceException ex){
        ApiError apiError = new ApiError(ex.getLocalizedMessage(),HttpStatus.CONFLICT,null);
        return new ResponseEntity<>(apiError,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .toList();

        ApiError apiError = new ApiError("Input validation failed",HttpStatus.BAD_REQUEST,errors);
        return  new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceCreationException.class)
    public ResponseEntity<ApiError> handleResourceCreationException(ResourceCreationException ex){
        ApiError apiError = new ApiError(ex.getLocalizedMessage(),HttpStatus.INTERNAL_SERVER_ERROR,null);
        return new ResponseEntity<>(apiError,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnAuthorisedException.class)
    public ResponseEntity<ApiError> handleUnAuthorisedException(UnAuthorisedException ex){
        ApiError apiError = new ApiError(ex.getLocalizedMessage(), HttpStatus.UNAUTHORIZED,null);
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(InvalidAppointmentException.class)
    public ResponseEntity<ApiError> handleInvalidAppointmentException(InvalidAppointmentException ex){
        ApiError apiError = new ApiError(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST,null);
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ApiError> handleInvalidPaymentException(InvalidPaymentException ex){
        ApiError apiError = new ApiError(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST,null);
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiError> handlePaymentException(PaymentException ex){
        ApiError apiError = new ApiError(ex.getLocalizedMessage(), HttpStatus.BAD_GATEWAY,null);
        return new ResponseEntity<>(apiError,HttpStatus.BAD_GATEWAY);
    }



    @ExceptionHandler(InvalidApprovalStatusException .class)
    public ResponseEntity<ApiError> handleInvalidApprovalStatusException (InvalidApprovalStatusException  ex){
        ApiError apiError = new ApiError(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST,null);
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(InvalidDoctorClinicActionException.class)
    public ResponseEntity<ApiError> handleInvalidDoctorClinicActionException(InvalidDoctorClinicActionException ex) {
        ApiError apiError = new ApiError(ex.getLocalizedMessage(), HttpStatus.CONFLICT, null);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(VideoConsultationException.class)
    public ResponseEntity<ApiError> handleVideoConsultationException(
            VideoConsultationException ex) {

        ApiError apiError = new ApiError(
                ex.getMessage(),
                ex.getStatus(),
                null
        );

        return new ResponseEntity<>(apiError, ex.getStatus());
    }

}
