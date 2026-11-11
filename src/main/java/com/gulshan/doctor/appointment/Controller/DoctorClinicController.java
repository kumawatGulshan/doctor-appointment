package com.gulshan.doctor.appointment.Controller;

import com.gulshan.doctor.appointment.Entity.Enums.DoctorClinicAction;
import com.gulshan.doctor.appointment.RequestDTO.DoctorClinicOfferRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.DoctorClinicResponseRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorClinicResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.DoctorClinicServiceInterface;
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
@RequestMapping("/doctor-clinics")
public class DoctorClinicController {

    private final DoctorClinicServiceInterface doctorClinicServiceInterface;

    // Clinic Owner sends an association offer.
    @PostMapping("/clinics/{clinicId}/doctors/{doctorId}")
    @Secured("ROLE_CLINIC_OWNER")
    @PreAuthorize("@doctorClinicSecurity.isClinicOwner(#clinicId)")
    public ResponseEntity<ApiResponseDto<DoctorClinicResponseDto>>sendOffer(@PathVariable Long clinicId,
                                          @PathVariable Long doctorId, @Valid @RequestBody DoctorClinicOfferRequestDto requestDto) {

        DoctorClinicResponseDto responseDto = doctorClinicServiceInterface.sendOffer(clinicId, doctorId, requestDto);
        ApiResponseDto<DoctorClinicResponseDto> response =
                new ApiResponseDto<>("Doctor association offer sent successfully.", responseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Doctor views incoming pending offers.
    @GetMapping("/pending")
    @Secured("ROLE_DOCTOR")
    public ResponseEntity<ApiResponseDto<List<DoctorClinicResponseDto>>> getPendingOffers() {
        List<DoctorClinicResponseDto> offers = doctorClinicServiceInterface.getPendingOffers();
        ApiResponseDto<List<DoctorClinicResponseDto>> response =
                new ApiResponseDto<>("Pending doctor-clinic offers retrieved successfully.", offers);
        return ResponseEntity.ok(response);
    }

    // Doctor accepts or rejects an offer using one endpoint.
    @PatchMapping("/{associationId}/respond")
    @Secured("ROLE_DOCTOR")
    @PreAuthorize("@doctorClinicSecurity.isOfferReceivedByLoggedInDoctor(#associationId)")
    public ResponseEntity<ApiResponseDto<DoctorClinicResponseDto>> respondToOffer(
            @PathVariable Long associationId, @Valid @RequestBody DoctorClinicResponseRequestDto requestDto) {

        DoctorClinicResponseDto responseDto = doctorClinicServiceInterface.respondToOffer(associationId, requestDto.getAction());
        String message;
        if (requestDto.getAction() == DoctorClinicAction.ACCEPT) {
            message = "Clinic association accepted successfully.";
        } else {
            message = "Clinic association offer rejected.";
        }
        ApiResponseDto<DoctorClinicResponseDto> response = new ApiResponseDto<>(message, responseDto);
        return ResponseEntity.ok(response);
    }

    // Clinic Owner views active doctors associated with their clinic.
    @GetMapping("/clinics/{clinicId}/doctors")
    @Secured("ROLE_CLINIC_OWNER")
    @PreAuthorize("@clinicSecurity.isOwnerOfClinic(#clinicId)")
    public ResponseEntity<ApiResponseDto<List<DoctorClinicResponseDto>>> getClinicDoctors(@PathVariable Long clinicId) {
        List<DoctorClinicResponseDto> doctors = doctorClinicServiceInterface.getClinicDoctors(clinicId);
        ApiResponseDto<List<DoctorClinicResponseDto>> response =
                new ApiResponseDto<>("Clinic doctors retrieved successfully.", doctors);
        return ResponseEntity.ok(response);
    }



    @PatchMapping("/{associationId}/remove")
    @Secured("ROLE_CLINIC_OWNER")
    @PreAuthorize("@doctorClinicSecurity.isOwnerOfAssociation(#associationId)")
    public ResponseEntity<String> ownerRemoveDoctor(@PathVariable Long associationId) {
        String response = doctorClinicServiceInterface.ownerRemoveDoctor(associationId);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{associationId}/leave")
    @Secured("ROLE_DOCTOR")
    @PreAuthorize("@doctorClinicSecurity.isDoctorOfAssociation(#associationId)")
    public ResponseEntity<String> doctorLeaveClinic(@PathVariable Long associationId) {
        String response = doctorClinicServiceInterface.doctorLeaveClinic(associationId);
        return ResponseEntity.ok(response);
    }
}