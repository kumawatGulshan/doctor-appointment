package com.gulshan.doctor.appointment.RequestDTO;

import com.gulshan.doctor.appointment.Entity.Enums.DoctorClinicAction;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorClinicResponseRequestDto {

    @NotNull(message = "Action is required")
    private DoctorClinicAction action;
}