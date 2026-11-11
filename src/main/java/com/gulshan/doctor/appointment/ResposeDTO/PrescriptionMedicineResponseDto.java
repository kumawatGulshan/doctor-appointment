package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.DoseUnit;
import com.gulshan.doctor.appointment.Entity.Enums.FoodInstruction;
import com.gulshan.doctor.appointment.Entity.Enums.MedicineType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrescriptionMedicineResponseDto {

    private Long medicineId;
    private String medicineName;
    private MedicineType medicineType;
    private String dosage;

    private BigDecimal morningDose;
    private BigDecimal noonDose;
    private BigDecimal eveningDose;

    private DoseUnit doseUnit;
    private Integer durationDays;
    private FoodInstruction foodInstruction;
    private String specialInstructions;
}