package com.gulshan.doctor.appointment.Controller;

import com.gulshan.doctor.appointment.RequestDTO.MedicalRecordRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.MedicalRecordResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.MedicalRecordServiceInterface;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/medical-records")
public class MedicalRecordController {

    private final MedicalRecordServiceInterface medicalRecordService;

    // Doctor creates a record for an appointment.
    @PostMapping("/appointments/{appointmentId}")
    @Secured("ROLE_DOCTOR")
    public ResponseEntity<ApiResponseDto<MedicalRecordResponseDto>>
    createMedicalRecord(
            @PathVariable Long appointmentId,
            @Valid @RequestBody MedicalRecordRequestDto request) {

        MedicalRecordResponseDto response =
                medicalRecordService.createMedicalRecord(
                        appointmentId,
                        request
                );

        return new ResponseEntity<>(
                new ApiResponseDto<>(
                        "Medical record created successfully",
                        response
                ),
                HttpStatus.CREATED
        );
    }

    // Only the creator doctor can update the record.
    @PutMapping("/{recordId}")
    @Secured("ROLE_DOCTOR")
    public ResponseEntity<ApiResponseDto<MedicalRecordResponseDto>>
    updateMedicalRecord(
            @PathVariable Long recordId,
            @Valid @RequestBody MedicalRecordRequestDto request) {

        MedicalRecordResponseDto response =
                medicalRecordService.updateMedicalRecord(
                        recordId,
                        request
                );

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Medical record updated successfully",
                        response
                )
        );
    }

    // Patient, creator doctor and Admin can view a record.
    @GetMapping("/{recordId}")
    @Secured({"ROLE_PATIENT", "ROLE_DOCTOR", "ROLE_ADMIN"})
    public ResponseEntity<ApiResponseDto<MedicalRecordResponseDto>>
    getMedicalRecord(@PathVariable Long recordId) {

        MedicalRecordResponseDto response =
                medicalRecordService.getMedicalRecord(recordId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Medical record fetched successfully",
                        response
                )
        );
    }

    // Retrieve records belonging to a patient.
    @GetMapping("/patients/{patientId}")
    @Secured({"ROLE_PATIENT", "ROLE_DOCTOR", "ROLE_ADMIN"})
    public ResponseEntity<ApiResponseDto<List<MedicalRecordResponseDto>>>
    getPatientMedicalRecords(@PathVariable Long patientId) {

        List<MedicalRecordResponseDto> response =
                medicalRecordService.getPatientMedicalRecords(patientId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Patient medical records fetched successfully",
                        response
                )
        );
    }
}