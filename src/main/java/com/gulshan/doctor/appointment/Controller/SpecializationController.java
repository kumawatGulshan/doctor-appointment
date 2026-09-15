package com.gulshan.doctor.appointment.Controller;


import com.gulshan.doctor.appointment.RequestDTO.SpecializationCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.SpecializationUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.SpecializationResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.SpecializationServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialization")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationServiceInterface specializationServiceInterface;

    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<String> addSpecialization(@RequestBody @Valid SpecializationCreateRequestDto specializationCreateRequestDto){
        String reponse = specializationServiceInterface.addSpecialization(specializationCreateRequestDto);
        return new ResponseEntity<>(reponse, HttpStatus.OK);
    }

    @PutMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<String> updateSpecialization(@RequestBody @Valid SpecializationUpdateRequestDto specializationUpdateRequestDto){
        String reponse = specializationServiceInterface.updateSpecialization(specializationUpdateRequestDto);
        return new ResponseEntity<>(reponse, HttpStatus.OK);
    }

    @GetMapping
    @Secured({"ROLE_ADMIN","ROLE_PATIENT","ROLE_DOCTOR"})
    public ResponseEntity<List<SpecializationResponseDto>> getAllSpecialization(){
        List<SpecializationResponseDto> response = specializationServiceInterface.getAllSpecialization();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/{specialization_id}")
    @Secured({"ROLE_ADMIN","ROLE_PATIENT","ROLE_DOCTOR"})
    public ResponseEntity<SpecializationResponseDto> getSpecializationById(@PathVariable Long specialization_id){
        SpecializationResponseDto response = specializationServiceInterface.getSpecializationById(specialization_id);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
