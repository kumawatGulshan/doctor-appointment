package com.gulshan.doctor.appointment.Controller;

import com.gulshan.doctor.appointment.RequestDTO.PrescriptionRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.PrescriptionResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.PrescriptionServiceInterface;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final PrescriptionServiceInterface prescriptionService;

    // Create prescription for an existing medical record.
    @PostMapping("/medical-records/{recordId}")
    @Secured("ROLE_DOCTOR")
    public ResponseEntity<ApiResponseDto<PrescriptionResponseDto>>
    createPrescription(
            @PathVariable Long recordId,
            @Valid @RequestBody PrescriptionRequestDto request) {

        PrescriptionResponseDto response =
                prescriptionService.createPrescription(
                        recordId,
                        request
                );

        return new ResponseEntity<>(
                new ApiResponseDto<>(
                        "Prescription created successfully",
                        response
                ),
                HttpStatus.CREATED
        );
    }

    // Only the prescribing doctor can update it.
    @PutMapping("/{prescriptionId}")
    @Secured("ROLE_DOCTOR")
    public ResponseEntity<ApiResponseDto<PrescriptionResponseDto>>
    updatePrescription(
            @PathVariable Long prescriptionId,
            @Valid @RequestBody PrescriptionRequestDto request) {

        PrescriptionResponseDto response =
                prescriptionService.updatePrescription(
                        prescriptionId,
                        request
                );

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Prescription updated successfully",
                        response
                )
        );
    }

    // Soft-delete prescription.
    @DeleteMapping("/{prescriptionId}")
    @Secured("ROLE_DOCTOR")
    public ResponseEntity<ApiResponseDto<Void>>
    deletePrescription(@PathVariable Long prescriptionId) {

        prescriptionService.deletePrescription(prescriptionId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Prescription deleted successfully",
                        null
                )
        );
    }

    // Patient, any registered doctor, or Admin can view it.
    @GetMapping("/{prescriptionId}")
    @Secured({"ROLE_PATIENT", "ROLE_DOCTOR", "ROLE_ADMIN"})
    public ResponseEntity<ApiResponseDto<PrescriptionResponseDto>>
    getPrescription(@PathVariable Long prescriptionId) {

        PrescriptionResponseDto response =
                prescriptionService.getPrescription(prescriptionId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Prescription fetched successfully",
                        response
                )
        );
    }

    // Patient sees own prescriptions; doctor can view any patient's history.
    @GetMapping("/patients/{patientId}")
    @Secured({"ROLE_PATIENT", "ROLE_DOCTOR", "ROLE_ADMIN"})
    public ResponseEntity<ApiResponseDto<List<PrescriptionResponseDto>>>
    getPatientPrescriptions(@PathVariable Long patientId) {

        List<PrescriptionResponseDto> response =
                prescriptionService.getPatientPrescriptions(patientId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Patient prescriptions fetched successfully",
                        response
                )
        );
    }

    // Admin only.
    @GetMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ApiResponseDto<List<PrescriptionResponseDto>>>
    getAllPrescriptions() {

        List<PrescriptionResponseDto> response =
                prescriptionService.getAllPrescriptions();

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "All prescriptions fetched successfully",
                        response
                )
        );
    }
}