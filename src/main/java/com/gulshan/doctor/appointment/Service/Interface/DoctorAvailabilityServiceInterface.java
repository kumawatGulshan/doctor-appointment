package com.gulshan.doctor.appointment.Service.Interface;

import com.gulshan.doctor.appointment.Entity.DoctorAvailability;
import com.gulshan.doctor.appointment.RequestDTO.DoctorAvailabilityCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.DoctorAvailabilityUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorAvailabilityCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorAvailabilityResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface DoctorAvailabilityServiceInterface {
    DoctorAvailabilityCreateResponseDto addAvailability(@Valid DoctorAvailabilityCreateRequestDto dto);

    List<DoctorAvailabilityResponseDto> getAllAvailabilitiesByUserId(Long userId);

    DoctorAvailabilityResponseDto getAvailabilityByDoctorAvailabilityId(Long doctorAvailabilityId);

    DoctorAvailability getAvailabilityEntityByDoctorAvailabilityId(Long doctorAvailabilityId);

    String deleteDoctorAvailabilityById(long doctorAvailabilityId);

    DoctorAvailabilityResponseDto updateDoctorAvailability(DoctorAvailabilityUpdateRequestDto doctorAvailabilityUpdateRequestDto);
}
