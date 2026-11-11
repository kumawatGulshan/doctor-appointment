package com.gulshan.doctor.appointment.Service.Interface;

import com.gulshan.doctor.appointment.RequestDTO.MedicalRecordRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.MedicalRecordResponseDto;

import java.util.List;

public interface MedicalRecordServiceInterface {

    MedicalRecordResponseDto createMedicalRecord(
            Long appointmentId,
            MedicalRecordRequestDto request
    );

    MedicalRecordResponseDto updateMedicalRecord(
            Long recordId,
            MedicalRecordRequestDto request
    );

    MedicalRecordResponseDto getMedicalRecord(Long recordId);

    List<MedicalRecordResponseDto> getPatientMedicalRecords(
            Long patientId
    );
}