package com.gulshan.doctor.appointment.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DoctorCreateRequestDto {
    @NotBlank(message = "Qualification degree is required")
    private String qualificationDegree;

    @NotNull(message = "Specialization ID is required")
    private Long specializationId;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotNull(message = "Consultation fee is required")
    @Positive(message = "Consultation fee must be greater than 0")
    private BigDecimal consultationFee;

    @NotNull(message = "Practice start date is required")
    @PastOrPresent(message = "Practice start date cannot be in the future")
    private LocalDate practiceStartDate;


    private String imageName;

    @Positive(message = "Average consultation time must be greater than 0")
    @NotNull(message = "Average consultation time is required(In minutes)")
    private Integer avgConsultationTime;
}
