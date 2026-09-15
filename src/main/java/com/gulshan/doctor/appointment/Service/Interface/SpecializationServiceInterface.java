package com.gulshan.doctor.appointment.Service.Interface;


import com.gulshan.doctor.appointment.RequestDTO.SpecializationCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.SpecializationUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.SpecializationResponseDto;

import java.util.List;

public interface SpecializationServiceInterface {
    String addSpecialization(SpecializationCreateRequestDto specializationCreateRequestDto);

    String updateSpecialization(SpecializationUpdateRequestDto specializationUpdateRequestDto);

    List<SpecializationResponseDto> getAllSpecialization();

    SpecializationResponseDto getSpecializationById(Long specializationId);
}
