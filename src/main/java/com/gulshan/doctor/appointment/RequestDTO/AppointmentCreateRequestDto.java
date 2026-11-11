package com.gulshan.doctor.appointment.RequestDTO;

import com.gulshan.doctor.appointment.Entity.Enums.AppointmentType;
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

    @NotNull(message = "Doctor availability is required")
    private Long doctorAvailabilityId;

    @FutureOrPresent(message = "Appointment date cannot be in the past")
    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;

    @NotNull(message = "Appointment type is required")
    private AppointmentType appointmentType;
}