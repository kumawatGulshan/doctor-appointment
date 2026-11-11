package com.gulshan.doctor.appointment.Controller;

import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.VideoConsultationJoinResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.VideoConsultationResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.VideoConsultationServiceInterface;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video-consultations")
public class VideoConsultationController {

    private final VideoConsultationServiceInterface
            videoConsultationServiceInterface;

    @GetMapping("/{appointmentId}")
    public ResponseEntity<ApiResponseDto<VideoConsultationResponseDto>>
    getMeetingDetails(@PathVariable Long appointmentId) {

        VideoConsultationResponseDto response =
                videoConsultationServiceInterface
                        .getMeetingDetails(appointmentId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Video consultation details retrieved successfully",
                        response
                )
        );
    }

    @PostMapping("/{appointmentId}/join")
    public ResponseEntity<ApiResponseDto<VideoConsultationJoinResponseDto>>
    joinConsultation(@PathVariable Long appointmentId) {

        VideoConsultationJoinResponseDto response =
                videoConsultationServiceInterface.joinConsultation(appointmentId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Consultation joined successfully",
                        response
                )
        );
    }

    @PostMapping("/{appointmentId}/end")
    public ResponseEntity<ApiResponseDto<VideoConsultationResponseDto>>
    endConsultation(@PathVariable Long appointmentId) {

        VideoConsultationResponseDto response =
                videoConsultationServiceInterface
                        .endConsultation(appointmentId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        "Consultation ended successfully",
                        response
                )
        );
    }
}