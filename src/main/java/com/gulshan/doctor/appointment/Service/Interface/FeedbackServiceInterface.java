
package com.gulshan.doctor.appointment.Service.Interface;

import com.gulshan.doctor.appointment.RequestDTO.FeedbackRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.FeedbackResponseDto;

import java.util.List;

public interface FeedbackServiceInterface {

    FeedbackResponseDto createFeedback(
            Long appointmentId,
            FeedbackRequestDto request
    );

    FeedbackResponseDto updateFeedback(
            Long feedbackId,
            FeedbackRequestDto request
    );

    void deleteFeedback(Long feedbackId);

    List<FeedbackResponseDto> getDoctorFeedback(Long doctorId);

    Double getDoctorAverageRating(Long doctorId);
}
