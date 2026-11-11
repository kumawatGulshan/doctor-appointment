package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.*;
import com.gulshan.doctor.appointment.Entity.Enums.AppointmentStatus;
import com.gulshan.doctor.appointment.Entity.Enums.Roles;
import com.gulshan.doctor.appointment.Exceptions.*;
import com.gulshan.doctor.appointment.Repository.*;
import com.gulshan.doctor.appointment.RequestDTO.MedicalRecordRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.MedicalRecordResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.MedicalRecordServiceInterface;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl
        implements MedicalRecordServiceInterface {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @Override
    @Transactional
    public MedicalRecordResponseDto createMedicalRecord(
            Long appointmentId,
            MedicalRecordRequestDto request) {

        User user = getCurrentUser();

        Doctor doctor = doctorRepository.findByUser_Id(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getDoctor().getDoctorId()
                .equals(doctor.getDoctorId())) {

            throw new UnAuthorisedException(
                    "You are not authorized to create a medical record for this appointment"
            );
        }

        AppointmentStatus status = appointment.getAppointmentStatus();

        if (status != AppointmentStatus.IN_PROGRESS
                && status != AppointmentStatus.COMPLETED) {

            throw new InvalidAppointmentException(
                    "Medical record can only be created for an in-progress or completed appointment"
            );
        }

        if (medicalRecordRepository
                .existsByAppointment_AppointmentId(appointmentId)) {

            throw new DuplicateResourceException(
                    "Medical record already exists for this appointment"
            );
        }

        MedicalRecord record = new MedicalRecord();

        record.setAppointment(appointment);
        record.setDoctor(doctor);
        record.setDiagnosis(request.getDiagnosis());
        record.setSymptoms(request.getSymptoms());
        record.setNotes(request.getNotes());

        MedicalRecord savedRecord =
                medicalRecordRepository.save(record);

        return mapToResponse(savedRecord);
    }

    @Override
    @Transactional
    public MedicalRecordResponseDto updateMedicalRecord(
            Long recordId,
            MedicalRecordRequestDto request) {

        User user = getCurrentUser();

        Doctor doctor = doctorRepository.findByUser_Id(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found"));

        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Medical record not found"));

        if (!record.getDoctor().getDoctorId()
                .equals(doctor.getDoctorId())) {

            throw new UnAuthorisedException(
                    "Only the doctor who created this medical record can update it"
            );
        }

        record.setDiagnosis(request.getDiagnosis());
        record.setSymptoms(request.getSymptoms());
        record.setNotes(request.getNotes());

        return mapToResponse(
                medicalRecordRepository.save(record)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponseDto getMedicalRecord(Long recordId) {

        User user = getCurrentUser();

        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Medical record not found"));

        boolean authorized = false;

        if (user.getRoles().contains(Roles.ADMIN)) {
            authorized = true;
        }

        if (user.getRoles().contains(Roles.PATIENT)) {

            Patient patient = patientRepository.findByUser_Id(user.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Patient profile not found"));

            authorized = record.getAppointment().getPatient()
                    .getPatientId().equals(patient.getPatientId());
        }

        if (user.getRoles().contains(Roles.DOCTOR)) {

            Doctor doctor = doctorRepository.findByUser_Id(user.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Doctor profile not found"));

            authorized = record.getDoctor().getDoctorId()
                    .equals(doctor.getDoctorId());
        }

        if (!authorized) {
            throw new UnAuthorisedException(
                    "You are not authorized to view this medical record"
            );
        }

        return mapToResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponseDto> getPatientMedicalRecords(
            Long patientId) {

        User user = getCurrentUser();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found"));

        List<MedicalRecord> records =
                medicalRecordRepository
                        .findByAppointment_Patient_PatientId(patientId);

        if (user.getRoles().contains(Roles.ADMIN)) {
            return records.stream().map(this::mapToResponse).toList();
        }

        if (user.getRoles().contains(Roles.PATIENT)) {

            Patient currentPatient = patientRepository
                    .findByUser_Id(user.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Patient profile not found"));

            if (!currentPatient.getPatientId().equals(patientId)) {
                throw new UnAuthorisedException(
                        "You can only view your own medical records"
                );
            }

            return records.stream().map(this::mapToResponse).toList();
        }

        if (user.getRoles().contains(Roles.DOCTOR)) {

            Doctor doctor = doctorRepository.findByUser_Id(user.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Doctor profile not found"));

            return records.stream()
                    .filter(record -> record.getDoctor().getDoctorId()
                            .equals(doctor.getDoctorId()))
                    .map(this::mapToResponse)
                    .toList();
        }

        throw new UnAuthorisedException(
                "You are not authorized to view medical records"
        );
    }

    private MedicalRecordResponseDto mapToResponse(
            MedicalRecord record) {

        MedicalRecordResponseDto response =
                new MedicalRecordResponseDto();

        Appointment appointment = record.getAppointment();

        response.setRecordId(record.getRecordId());
        response.setAppointmentId(appointment.getAppointmentId());

        response.setDoctorId(record.getDoctor().getDoctorId());
        response.setDoctorName(record.getDoctor().getUser().getName());

        response.setPatientId(
                appointment.getPatient().getPatientId()
        );

        response.setPatientName(
                appointment.getPatient().getUser().getName()
        );

        response.setAppointmentDate(appointment.getAppointmentDate());

        response.setDiagnosis(record.getDiagnosis());
        response.setSymptoms(record.getSymptoms());
        response.setNotes(record.getNotes());

        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());

        return response;
    }
}