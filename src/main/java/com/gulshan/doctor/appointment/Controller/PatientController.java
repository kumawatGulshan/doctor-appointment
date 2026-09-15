package com.gulshan.doctor.appointment.Controller;


import com.gulshan.doctor.appointment.RequestDTO.PatientCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.PatientUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.PatientCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.PatientResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.PatientUpdateResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.PatientServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {

    private final PatientServiceInterface patientServiceInterface;

    @PostMapping
    @Secured("ROLE_PATIENT")
    public ResponseEntity<ApiResponseDto<PatientCreateResponseDto>> addPatientDetails(@RequestBody @Valid PatientCreateRequestDto patientCreateRequestDto){
        PatientCreateResponseDto addPatientDetails = patientServiceInterface.addPatientDetails(patientCreateRequestDto);
        ApiResponseDto<PatientCreateResponseDto> response= new ApiResponseDto<>("Patient Details added successfully.",addPatientDetails);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{user_id}")
    @Secured({"ROLE_ADMIN","ROLE_PATIENT","ROLE_DOCTOR"})
    public ResponseEntity<ApiResponseDto<PatientResponseDto>> getPatientById(@PathVariable long user_id){
       PatientResponseDto patientResponseDto =  patientServiceInterface.getPatientByUserId(user_id);
       ApiResponseDto<PatientResponseDto> response = new ApiResponseDto<>("Patient details found",patientResponseDto);
       return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping
    @Secured("ROLE_PATIENT")
    public ResponseEntity<PatientUpdateResponseDto> updatePatient(@RequestBody @Valid PatientUpdateRequestDto patientUpdateRequestDto){
        PatientUpdateResponseDto response = patientServiceInterface.updatePateint(patientUpdateRequestDto);
    return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public ResponseEntity<ApiResponseDto<List<PatientResponseDto>>> getAllPatient(){
        List<PatientResponseDto> patientResponseDtoList = patientServiceInterface.getAllPatients();
        ApiResponseDto<List<PatientResponseDto>> responseList = new ApiResponseDto<>("Patient List found",patientResponseDtoList);
        return new ResponseEntity<>(responseList,HttpStatus.OK);
    }

}
