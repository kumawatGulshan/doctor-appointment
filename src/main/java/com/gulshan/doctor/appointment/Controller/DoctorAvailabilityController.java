package com.gulshan.doctor.appointment.Controller;

import com.gulshan.doctor.appointment.RequestDTO.DoctorAvailabilityCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.DoctorAvailabilityUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorAvailabilityCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorAvailabilityResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.DoctorAvailabilityServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/availability")
public class DoctorAvailabilityController {

    private final DoctorAvailabilityServiceInterface doctorAvailabilityServiceInterface;

    @Secured("ROLE_DOCTOR")
    @PostMapping
    public ResponseEntity<ApiResponseDto<DoctorAvailabilityCreateResponseDto>> addAvailability(@RequestBody @Valid
                                                                                                   DoctorAvailabilityCreateRequestDto dto){
        DoctorAvailabilityCreateResponseDto responseDto = doctorAvailabilityServiceInterface.addAvailability(dto);
        ApiResponseDto<DoctorAvailabilityCreateResponseDto> apiResponse = new ApiResponseDto<>("Availability added successfully",responseDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/all/{user_id}")
    public ResponseEntity<ApiResponseDto<List<DoctorAvailabilityResponseDto>>> getAllAvailabilitiesByUserId(@PathVariable Long user_id){
       List<DoctorAvailabilityResponseDto> list =  doctorAvailabilityServiceInterface.getAllAvailabilitiesByUserId(user_id);
       ApiResponseDto<List<DoctorAvailabilityResponseDto>> responseList = new ApiResponseDto<>("Doctor availability list found",list);
       return new ResponseEntity<>(responseList,HttpStatus.OK);
    }

    @GetMapping("/{doctor_availability_id}")
    public ResponseEntity<ApiResponseDto<DoctorAvailabilityResponseDto>> getAvailabilityByDoctorAvailabilityId(@PathVariable Long doctor_availability_id){
        DoctorAvailabilityResponseDto doctorAvailabilityResponseDto =  doctorAvailabilityServiceInterface.getAvailabilityByDoctorAvailabilityId(doctor_availability_id);
        ApiResponseDto<DoctorAvailabilityResponseDto> response = new ApiResponseDto<>("Doctor availability found",doctorAvailabilityResponseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/{doctor_availability_id}")
    @PreAuthorize("@doctorAvailabilitySecurity.canUserDeleteThisAvailability(#doctor_availability_id) OR hasRole('ADMIN')")
    public ResponseEntity<String> deleteDoctorAvailabilityById(@PathVariable long doctor_availability_id){
        String response = doctorAvailabilityServiceInterface.deleteDoctorAvailabilityById(doctor_availability_id);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("@doctorAvailabilitySecurity.isOwnerOfAvailability(#doctorAvailabilityUpdateRequestDto)")
    public ResponseEntity<ApiResponseDto<DoctorAvailabilityResponseDto>> updateDoctorAvailability(@RequestBody @Valid DoctorAvailabilityUpdateRequestDto doctorAvailabilityUpdateRequestDto){
        DoctorAvailabilityResponseDto doctorAvailabilityResponseDto = doctorAvailabilityServiceInterface.updateDoctorAvailability(doctorAvailabilityUpdateRequestDto);
        ApiResponseDto<DoctorAvailabilityResponseDto> responseDto = new ApiResponseDto<>("Availability updated successfully",doctorAvailabilityResponseDto);
        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }


}
