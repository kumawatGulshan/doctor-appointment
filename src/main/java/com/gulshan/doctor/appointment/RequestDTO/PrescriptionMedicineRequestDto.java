
package com.gulshan.doctor.appointment.RequestDTO;

import com.gulshan.doctor.appointment.Entity.Enums.DoseUnit;
import com.gulshan.doctor.appointment.Entity.Enums.FoodInstruction;
import com.gulshan.doctor.appointment.Entity.Enums.MedicineType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrescriptionMedicineRequestDto {

    @NotBlank(message = "Medicine name is required")
    private String medicineName;

    @NotNull(message = "Medicine type is required")
    private MedicineType medicineType;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotNull(message = "Morning dose is required")
    @DecimalMin(value = "0.0", message = "Morning dose cannot be negative")
    private BigDecimal morningDose;

    @NotNull(message = "Noon dose is required")
    @DecimalMin(value = "0.0", message = "Noon dose cannot be negative")
    private BigDecimal noonDose;

    @NotNull(message = "Evening dose is required")
    @DecimalMin(value = "0.0", message = "Evening dose cannot be negative")
    private BigDecimal eveningDose;

    @NotNull(message = "Dose unit is required")
    private DoseUnit doseUnit;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least one day")
    private Integer durationDays;

    @NotNull(message = "Food instruction is required")
    private FoodInstruction foodInstruction;

    private String specialInstructions;
}
