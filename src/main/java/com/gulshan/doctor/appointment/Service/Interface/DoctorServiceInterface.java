package com.gulshan.doctor.appointment.Service.Interface;



import com.gulshan.doctor.appointment.RequestDTO.DoctorCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.DoctorUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DoctorServiceInterface {
    DoctorCreateResponseDto addDoctorDetails(String imageDir, DoctorCreateRequestDto doctorCreateRequestDto, MultipartFile imageFile)throws IOException;

    DoctorResponseDto getDoctorByUserId(Long userId);


    List<DoctorResponseDto> getAllDoctorsBySpecializationId(Long specializationId);

    DoctorResponseDto updateDoctorDetails(String imageDir, DoctorUpdateRequestDto doctorUpdateRequestDto, MultipartFile imageFile) throws IOException;
}
