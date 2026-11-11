package com.gulshan.doctor.appointment.Service.Interface;

import com.gulshan.doctor.appointment.Entity.Appointment;
import com.gulshan.doctor.appointment.ResposeDTO.VideoConsultationJoinResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.VideoConsultationResponseDto;

public interface VideoConsultationServiceInterface {

    void createMeetingForAppointment(Appointment appointment);

    VideoConsultationResponseDto getMeetingDetails(Long appointmentId);

    VideoConsultationJoinResponseDto joinConsultation(Long appointmentId);

    VideoConsultationResponseDto endConsultation(Long appointmentId);
}