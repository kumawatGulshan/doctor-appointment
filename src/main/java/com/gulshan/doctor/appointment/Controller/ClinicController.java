package com.gulshan.doctor.appointment.Controller;

import com.gulshan.doctor.appointment.Entity.Enums.ClinicApprovalStatus;
import com.gulshan.doctor.appointment.RequestDTO.ClinicCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.ClinicUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.ClinicCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.ClinicResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.ClinicServiceInterface;
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
@RequestMapping("/clinics")
public class ClinicController {

    private final ClinicServiceInterface clinicServiceInterface;

    @PostMapping
    @Secured("ROLE_CLINIC_OWNER")
    public ResponseEntity<ApiResponseDto<ClinicCreateResponseDto>> createClinic(@Valid @RequestBody ClinicCreateRequestDto requestDto){
        ClinicCreateResponseDto responseDto = clinicServiceInterface.createClinic(requestDto);
        ApiResponseDto<ClinicCreateResponseDto> response = new ApiResponseDto<>
                ("Clinic registered successfully. Waiting for Admin approval.", responseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my_clinics")
    @Secured("ROLE_CLINIC_OWNER")
    public ResponseEntity<ApiResponseDto<List<ClinicResponseDto>>> getMyClinics() {
        List<ClinicResponseDto> clinics = clinicServiceInterface.getMyClinics();
        ApiResponseDto<List<ClinicResponseDto>> response = new ApiResponseDto<>("Your clinics retrieved successfully.", clinics);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Secured({
            "ROLE_PATIENT",
            "ROLE_DOCTOR",
            "ROLE_CLINIC_OWNER",
            "ROLE_ADMIN"
    })
    public ResponseEntity<ApiResponseDto<List<ClinicResponseDto>>> getApprovedClinics() {
        List<ClinicResponseDto> clinics = clinicServiceInterface.getApprovedClinics();
        ApiResponseDto<List<ClinicResponseDto>> response = new ApiResponseDto<>("Approved clinics retrieved successfully.", clinics);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/pending")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ApiResponseDto<List<ClinicResponseDto>>> getPendingClinics() {
        List<ClinicResponseDto> clinics = clinicServiceInterface.getPendingClinics();
        ApiResponseDto<List<ClinicResponseDto>> response = new ApiResponseDto<>("Pending clinics retrieved successfully.", clinics);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{clinicId}/approval")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ClinicResponseDto> updateApprovalStatus(@PathVariable Long clinicId, @RequestParam ClinicApprovalStatus status) {
        ClinicResponseDto response = clinicServiceInterface.updateApprovalStatus(clinicId, status);
        return ResponseEntity.ok(response);
    }



    @PutMapping("/{clinicId}")
    @Secured("ROLE_CLINIC_OWNER")
    @PreAuthorize("@clinicSecurity.isOwnerOfClinic(#clinicId)")
    public ResponseEntity<ClinicResponseDto> updateClinic(@PathVariable Long clinicId, @Valid @RequestBody ClinicUpdateRequestDto requestDto) {
        ClinicResponseDto response = clinicServiceInterface.updateClinic(clinicId, requestDto);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{clinicId}")
    @Secured("ROLE_CLINIC_OWNER")
    @PreAuthorize("@clinicSecurity.isOwnerOfClinic(#clinicId)")
    public ResponseEntity<String> deleteClinic(
            @PathVariable Long clinicId) {

        String response =
                clinicServiceInterface.softDeleteClinic(clinicId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}