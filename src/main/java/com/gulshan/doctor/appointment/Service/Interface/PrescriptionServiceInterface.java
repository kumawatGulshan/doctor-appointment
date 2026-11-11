package com.gulshan.doctor.appointment.Service.Interface;

import com.gulshan.doctor.appointment.RequestDTO.PrescriptionRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.PrescriptionResponseDto;

import java.util.List;

public interface PrescriptionServiceInterface {

    PrescriptionResponseDto createPrescription(
            Long recordId,
            PrescriptionRequestDto request
    );

    PrescriptionResponseDto updatePrescription(
            Long prescriptionId,
            PrescriptionRequestDto request
    );

    void deletePrescription(Long prescriptionId);

    PrescriptionResponseDto getPrescription(Long prescriptionId);

    List<PrescriptionResponseDto> getPatientPrescriptions(
            Long patientId
    );

    List<PrescriptionResponseDto> getAllPrescriptions();
}