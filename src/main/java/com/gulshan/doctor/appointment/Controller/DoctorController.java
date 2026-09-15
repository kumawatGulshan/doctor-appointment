package com.gulshan.doctor.appointment.Controller;


import com.gulshan.doctor.appointment.RequestDTO.DoctorCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.DoctorUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.DoctorServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorServiceInterface doctorServiceInterface;

    @Value("${project.images}")
    private String imageDir;

    @Secured("ROLE_DOCTOR")
    @PostMapping
    public ResponseEntity<ApiResponseDto<DoctorCreateResponseDto>> addDoctorDetails(@RequestPart @Valid DoctorCreateRequestDto doctorCreateRequestDto,
                                                                                    @RequestPart MultipartFile imageFile) throws IOException {
        DoctorCreateResponseDto doctorCreateResponseDto = doctorServiceInterface.addDoctorDetails(imageDir,doctorCreateRequestDto,imageFile);
        ApiResponseDto<DoctorCreateResponseDto> response =new ApiResponseDto<>("Doctor details added successfully.",doctorCreateResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{user_id}")
    @Secured({"ROLE_PATIENT", "ROLE_DOCTOR", "ROLE_ADMIN"})
    public ResponseEntity<ApiResponseDto<DoctorResponseDto>> getDoctorUserId(@PathVariable Long user_id){
        DoctorResponseDto doctorResponseDto = doctorServiceInterface.getDoctorByUserId(user_id);
        ApiResponseDto<DoctorResponseDto> response =new ApiResponseDto<>("Doctor details found successfully.",doctorResponseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Change this method and filter by city also
    @GetMapping("/specialization/{specialization_id}")
    @Secured({"ROLE_PATIENT", "ROLE_DOCTOR", "ROLE_ADMIN"})
    public ResponseEntity<ApiResponseDto<List<DoctorResponseDto>>> getAllDoctorsBySpecializationId(@PathVariable Long specialization_id){
        List<DoctorResponseDto> doctorResponseDtosList = doctorServiceInterface.getAllDoctorsBySpecializationId(specialization_id);
        ApiResponseDto<List<DoctorResponseDto>> response = new ApiResponseDto<>("Doctor list found",doctorResponseDtosList);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping
    @Secured("ROLE_DOCTOR")
    public ResponseEntity<ApiResponseDto<DoctorResponseDto>> updateDoctorDetails(@RequestPart @Valid DoctorUpdateRequestDto doctorUpdateRequestDto,
                                                                                 @RequestPart MultipartFile imageFile) throws IOException{
        DoctorResponseDto doctorResponseDto = doctorServiceInterface.updateDoctorDetails(imageDir,doctorUpdateRequestDto,imageFile);
        ApiResponseDto<DoctorResponseDto> response = new ApiResponseDto<>("Doctor details updated successfully.",doctorResponseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }
}
