package com.gulshan.doctor.appointment.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SpecializationUpdateRequestDto {

    private Long specializationId;

    @NotBlank(message = "Specialization is required")
    private String specializationName;
}
