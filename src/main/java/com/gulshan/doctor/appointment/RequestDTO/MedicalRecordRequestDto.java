
package com.gulshan.doctor.appointment.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MedicalRecordRequestDto {

    @NotBlank(message = "Diagnosis is required")
    @Size(max = 500, message = "Diagnosis cannot exceed 500 characters")
    private String diagnosis;

    private String symptoms;

    private String notes;
}
