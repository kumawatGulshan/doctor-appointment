package com.gulshan.doctor.appointment.Service.Interface;

import com.gulshan.doctor.appointment.Entity.Enums.ClinicApprovalStatus;
import com.gulshan.doctor.appointment.RequestDTO.ClinicCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.ClinicUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ClinicCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.ClinicResponseDto;

import java.util.List;

public interface ClinicServiceInterface {

    ClinicCreateResponseDto createClinic(ClinicCreateRequestDto requestDto);

    List<ClinicResponseDto> getMyClinics();

    List<ClinicResponseDto> getApprovedClinics();

    List<ClinicResponseDto> getPendingClinics();

    ClinicResponseDto updateApprovalStatus(Long clinicId, ClinicApprovalStatus approvalStatus);

    ClinicResponseDto updateClinic(Long clinicId, ClinicUpdateRequestDto requestDto);

    String softDeleteClinic(Long clinicId);
}