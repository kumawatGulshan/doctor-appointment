package com.gulshan.doctor.appointment.ResposeDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class SpecializationResponseDto {
    private Long specializationId;

    @NotBlank(message = "Specialization is required")
    private String specializationName;

}
