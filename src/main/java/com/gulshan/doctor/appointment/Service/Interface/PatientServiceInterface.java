package com.gulshan.doctor.appointment.Service.Interface;


import com.gulshan.doctor.appointment.RequestDTO.PatientCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.PatientUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.PatientCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.PatientResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.PatientUpdateResponseDto;

import java.util.List;

public interface PatientServiceInterface {
    PatientCreateResponseDto addPatientDetails(PatientCreateRequestDto patientCreateRequestDto);

    PatientResponseDto getPatientByUserId(long userId);

    PatientUpdateResponseDto updatePateint(PatientUpdateRequestDto patientUpdateRequestDto);

    List<PatientResponseDto> getAllPatients();
}
