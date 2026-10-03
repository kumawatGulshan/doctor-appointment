package com.gulshan.doctor.appointment.RequestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
public class ScheduledAppointmentRequestDto {
    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
}
