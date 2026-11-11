package com.gulshan.doctor.appointment.ResposeDTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrescriptionResponseDto {

    private Long prescriptionId;
    private Long recordId;
    private Long appointmentId;

    private Long doctorId;
    private String doctorName;

    private Long patientId;
    private String patientName;

    private String notes;

    private List<PrescriptionMedicineResponseDto> medicines;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}