
package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.AppointmentStatus;
import com.gulshan.doctor.appointment.Entity.Enums.AppointmentType;
import com.gulshan.doctor.appointment.Entity.Enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentStatusResponseDto {

    private Long appointmentId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentType appointmentType;
    private AppointmentStatus appointmentStatus;
    private PaymentStatus paymentStatus;
}
