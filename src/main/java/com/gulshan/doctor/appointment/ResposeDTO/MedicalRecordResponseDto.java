package com.gulshan.doctor.appointment.ResposeDTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MedicalRecordResponseDto {

    private Long recordId;
    private Long appointmentId;

    private Long doctorId;
    private String doctorName;

    private Long patientId;
    private String patientName;

    private LocalDate appointmentDate;

    private String diagnosis;
    private String symptoms;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}