package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.AppointmentStatus;
import com.gulshan.doctor.appointment.Entity.Enums.PaymentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentCancelResponseDto {
    private Long appointmentId;

    private Long doctorId;
    private String doctorName;
    private String specialization;

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    private Integer tokenNo;

    private AppointmentStatus appointmentStatus;
    private PaymentStatus paymentStatus;
}
