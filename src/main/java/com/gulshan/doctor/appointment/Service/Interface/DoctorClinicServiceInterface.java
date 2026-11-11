package com.gulshan.doctor.appointment.Service.Interface;

import com.gulshan.doctor.appointment.Entity.Enums.DoctorClinicAction;
import com.gulshan.doctor.appointment.RequestDTO.DoctorClinicOfferRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorClinicResponseDto;

import java.util.List;

public interface DoctorClinicServiceInterface {

    DoctorClinicResponseDto sendOffer(Long clinicId, Long doctorId, DoctorClinicOfferRequestDto requestDto);

    List<DoctorClinicResponseDto> getPendingOffers();

    DoctorClinicResponseDto respondToOffer(Long associationId, DoctorClinicAction action);

    List<DoctorClinicResponseDto> getClinicDoctors(Long clinicId);

    String ownerRemoveDoctor(Long associationId);

    String doctorLeaveClinic(Long associationId);
}