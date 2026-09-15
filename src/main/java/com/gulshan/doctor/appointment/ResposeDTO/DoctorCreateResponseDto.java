package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Specialization;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DoctorCreateResponseDto {

    private String qualificationDegree;

    private Specialization specialization;

    private String licenseNumber;

    private BigDecimal consultationFee;

    private LocalDate practiceStartDate;

    private String imageName;

    private Integer avgConsultationTime;
}
