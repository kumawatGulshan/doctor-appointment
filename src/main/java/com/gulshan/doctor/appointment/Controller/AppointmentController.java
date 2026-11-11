package com.gulshan.doctor.appointment.Controller;

import com.gulshan.doctor.appointment.RequestDTO.AppointmentCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.ScheduledAppointmentRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.*;
import com.gulshan.doctor.appointment.Service.Interface.AppointmentServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentServiceInterface appointmentServiceInterface;

    @PostMapping("/book")
    public ResponseEntity<ApiResponseDto<AppointmentCreateResponseDto>> bookAppointment(@Valid @RequestBody AppointmentCreateRequestDto request) {
        AppointmentCreateResponseDto appointmentCreateResponseDto = appointmentServiceInterface.bookAppointment(request);
        ApiResponseDto<AppointmentCreateResponseDto> response =new ApiResponseDto<>("Appointment booked successfully.",appointmentCreateResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cancel/{appointmentId}")
    public ResponseEntity<ApiResponseDto<AppointmentCancelResponseDto>> cancelAppointment(@PathVariable Long appointmentId) {
        AppointmentCancelResponseDto appointmentCreateResponseDto = appointmentServiceInterface.cancelAppointment(appointmentId);
        ApiResponseDto<AppointmentCancelResponseDto> response =new ApiResponseDto<>("Appointment cancelled successfully. Refund is pending.",appointmentCreateResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/scheduled")
    @PreAuthorize("(hasRole('DOCTOR') AND @appointmentSecurity.canCurrentUserAccess(#userId)) OR hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<List<ScheduledAppointmentResponseDto>>>
    getAllScheduledAppointment(
            @RequestParam Long userId,
            @RequestParam(required = false) Long clinicId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime) {

        List<ScheduledAppointmentResponseDto> appointments =
                appointmentServiceInterface.scheduledAppointment(
                        clinicId, date, startTime, endTime
                );

        ApiResponseDto<List<ScheduledAppointmentResponseDto>> response =
                new ApiResponseDto<>(
                        "Scheduled appointments fetched successfully.",
                        appointments
                );

        return ResponseEntity.ok(response);
    }

    // Doctor starts a physical consultation.
    @PatchMapping("/{appointmentId}/start")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<AppointmentStatusResponseDto>>
    startPhysicalConsultation(
            @PathVariable Long appointmentId) {

        AppointmentStatusResponseDto response =
                appointmentServiceInterface.startPhysicalConsultation(
                        appointmentId
                );

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Physical consultation started successfully",
                        response
                )
        );
    }


    // Doctor ends a physical consultation.
    @PatchMapping("/{appointmentId}/end")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponseDto<AppointmentStatusResponseDto>>
    endPhysicalConsultation(
            @PathVariable Long appointmentId) {

        AppointmentStatusResponseDto response =
                appointmentServiceInterface.endPhysicalConsultation(
                        appointmentId
                );

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Physical consultation completed successfully",
                        response
                )
        );
    }

}
