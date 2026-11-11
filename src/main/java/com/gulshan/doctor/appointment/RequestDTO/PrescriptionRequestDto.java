
package com.gulshan.doctor.appointment.RequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequestDto {

    private String notes;

    @NotEmpty(message = "At least one medicine is required")
    @Valid
    private List<PrescriptionMedicineRequestDto> medicines;
}
