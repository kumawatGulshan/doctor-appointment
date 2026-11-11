package com.gulshan.doctor.appointment.RequestDTO;

import com.gulshan.doctor.appointment.Entity.Enums.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Data
@Getter
@Setter
public class DoctorAvailabilityCreateRequestDto {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    private Long clinicId;
}
