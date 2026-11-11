
package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.VideoConsultationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoConsultationJoinResponseDto {

    private Long appointmentId;
    private String meetingRoomId;
    private String meetingUrl;
    private String token;
    private VideoConsultationStatus status;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
