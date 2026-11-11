package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.DoctorClinicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorClinicResponseDto {

    private Long id;

    private Long doctorId;
    private Long doctorUserId;
    private String doctorName;

    private Long clinicId;
    private String clinicName;

    private BigDecimal consultationFee;

    private DoctorClinicStatus associationStatus;

    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    private Boolean physicalConsultationEnabled;
    private Boolean videoConsultationEnabled;
}