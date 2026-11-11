
package com.gulshan.doctor.appointment.Controller;

import com.gulshan.doctor.appointment.RequestDTO.FeedbackRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.FeedbackResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.FeedbackServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackServiceInterface feedbackService;

    // Patient submits feedback
    @PostMapping("/appointments/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponseDto<FeedbackResponseDto>> createFeedback(
            @PathVariable Long appointmentId,
            @Valid @RequestBody FeedbackRequestDto request) {

        FeedbackResponseDto response =
                feedbackService.createFeedback(appointmentId, request);

        return ResponseEntity.ok(
                new ApiResponseDto<>("Feedback submitted successfully", response));
    }

    // Patient updates their feedback
    @PutMapping("/{feedbackId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponseDto<FeedbackResponseDto>> updateFeedback(
            @PathVariable Long feedbackId,
            @Valid @RequestBody FeedbackRequestDto request) {

        FeedbackResponseDto response =
                feedbackService.updateFeedback(feedbackId, request);

        return ResponseEntity.ok(
                new ApiResponseDto<>("Feedback updated successfully", response));
    }

    // Patient deletes their feedback
    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponseDto<Void>> deleteFeedback(
            @PathVariable Long feedbackId) {

        feedbackService.deleteFeedback(feedbackId);

        return ResponseEntity.ok(
                new ApiResponseDto<>("Feedback deleted successfully", null));
    }

    // Get all feedback for a doctor
    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<ApiResponseDto<List<FeedbackResponseDto>>> getDoctorFeedback(
            @PathVariable Long doctorId) {

        List<FeedbackResponseDto> response =
                feedbackService.getDoctorFeedback(doctorId);

        return ResponseEntity.ok(
                new ApiResponseDto<>("Doctor feedback fetched successfully", response));
    }

    // Get doctor's average rating
    @GetMapping("/doctors/{doctorId}/rating")
    public ResponseEntity<ApiResponseDto<Double>> getDoctorAverageRating(
            @PathVariable Long doctorId) {

        Double averageRating =
                feedbackService.getDoctorAverageRating(doctorId);

        return ResponseEntity.ok(
                new ApiResponseDto<>("Doctor average rating fetched successfully",
                        averageRating));
    }
}
