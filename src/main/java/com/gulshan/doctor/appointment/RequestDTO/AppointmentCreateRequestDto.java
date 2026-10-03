package com.gulshan.doctor.appointment.RequestDTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentCreateRequestDto {
    @NotNull
    private Long doctorAvailabilityId;

    @FutureOrPresent(message = "Appointment date cannot be in the past")
    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime appointmentTime;
}
