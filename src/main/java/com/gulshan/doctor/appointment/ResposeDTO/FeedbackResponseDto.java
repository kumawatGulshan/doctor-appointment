
package com.gulshan.doctor.appointment.ResposeDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackResponseDto {

    private Long feedbackId;

    private Long appointmentId;

    private Long patientId;

    private String patientName;

    private Long doctorId;

    private String doctorName;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
