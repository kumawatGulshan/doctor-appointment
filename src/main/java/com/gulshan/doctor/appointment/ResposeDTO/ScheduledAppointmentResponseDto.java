package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.AppointmentStatus;
import com.gulshan.doctor.appointment.Entity.Enums.AppointmentType;
import com.gulshan.doctor.appointment.Entity.Enums.PaymentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduledAppointmentResponseDto {

    private Long appointmentId;

    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    private AppointmentStatus appointmentStatus;
    private PaymentStatus paymentStatus;
    private AppointmentType appointmentType;

    private String patientName;
    private String mobNumber;
}