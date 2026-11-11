package com.gulshan.doctor.appointment.RequestDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DoctorClinicOfferRequestDto {

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(
            value = "0.01",
            message = "Consultation fee must be greater than zero"
    )
    private BigDecimal consultationFee;

    @NotNull(message = "Physical consultation preference is required")
    private Boolean physicalConsultationEnabled;

    @NotNull(message = "Video consultation preference is required")
    private Boolean videoConsultationEnabled;
}