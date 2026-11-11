package com.gulshan.doctor.appointment.Service.Interface;

import com.gulshan.doctor.appointment.RequestDTO.AppointmentCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.ScheduledAppointmentRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.AppointmentCancelResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.AppointmentCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.AppointmentStatusResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.ScheduledAppointmentResponseDto;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentServiceInterface {
    AppointmentCreateResponseDto bookAppointment(@Valid AppointmentCreateRequestDto request);


    AppointmentCancelResponseDto cancelAppointment(Long appointmentId);


    List<ScheduledAppointmentResponseDto> scheduledAppointment(
            Long clinicId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );

    AppointmentStatusResponseDto startPhysicalConsultation(Long appointmentId);

    AppointmentStatusResponseDto endPhysicalConsultation(Long appointmentId);
}
