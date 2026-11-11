
package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.*;
import com.gulshan.doctor.appointment.Entity.Enums.Roles;
import com.gulshan.doctor.appointment.Exceptions.*;
import com.gulshan.doctor.appointment.Repository.*;
import com.gulshan.doctor.appointment.RequestDTO.PrescriptionMedicineRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.PrescriptionRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.*;
import com.gulshan.doctor.appointment.Service.Interface.PrescriptionServiceInterface;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl
        implements PrescriptionServiceInterface {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // Only the doctor who created the MedicalRecord can manage its prescription.
    private Doctor getCurrentDoctor() {

        User user = getCurrentUser();

        return doctorRepository.findByUser_Id(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found"));
    }

    @Override
    @Transactional
    public PrescriptionResponseDto createPrescription(
            Long recordId,
            PrescriptionRequestDto request) {

        Doctor doctor = getCurrentDoctor();

        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Medical record not found"));

        if (!record.getDoctor().getDoctorId()
                .equals(doctor.getDoctorId())) {

            throw new UnAuthorisedException(
                    "Only the doctor who created this medical record can prescribe medicines"
            );
        }

        // Includes soft-deleted prescriptions because the relationship is unique.
        if (prescriptionRepository
                .existsByMedicalRecord_RecordId(recordId)) {

            throw new DuplicateResourceException(
                    "A prescription already exists for this medical record"
            );
        }

        Prescription prescription = new Prescription();

        prescription.setMedicalRecord(record);
        prescription.setNotes(request.getNotes());

        addMedicines(prescription, request.getMedicines());

        Prescription savedPrescription =
                prescriptionRepository.save(prescription);

        return mapToResponse(savedPrescription);
    }

    @Override
    @Transactional
    public PrescriptionResponseDto updatePrescription(
            Long prescriptionId,
            PrescriptionRequestDto request) {

        Doctor doctor = getCurrentDoctor();

        Prescription prescription =
                prescriptionRepository
                        .findByPrescriptionIdAndIsDeletedFalse(prescriptionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Active prescription not found"
                                ));

        if (!prescription.getMedicalRecord().getDoctor()
                .getDoctorId().equals(doctor.getDoctorId())) {

            throw new UnAuthorisedException(
                    "Only the prescribing doctor can update this prescription"
            );
        }

        prescription.setNotes(request.getNotes());

        // Keep the existing Hibernate-managed collection.
        prescription.getMedicines().clear();

        addMedicines(prescription, request.getMedicines());

        return mapToResponse(
                prescriptionRepository.save(prescription)
        );
    }

    @Override
    @Transactional
    public void deletePrescription(Long prescriptionId) {

        Doctor doctor = getCurrentDoctor();

        Prescription prescription =
                prescriptionRepository
                        .findByPrescriptionIdAndIsDeletedFalse(prescriptionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Active prescription not found"
                                ));

        if (!prescription.getMedicalRecord().getDoctor()
                .getDoctorId().equals(doctor.getDoctorId())) {

            throw new UnAuthorisedException(
                    "Only the prescribing doctor can delete this prescription"
            );
        }

        prescription.setDeleted(true);
        prescription.setDeletedAt(LocalDateTime.now());

        prescriptionRepository.save(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponseDto getPrescription(Long prescriptionId) {

        User user = getCurrentUser();

        Prescription prescription =
                prescriptionRepository
                        .findByPrescriptionIdAndIsDeletedFalse(prescriptionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Prescription not found"
                                ));

        authorizePrescriptionView(user, prescription);

        return mapToResponse(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getPatientPrescriptions(
            Long patientId) {

        User user = getCurrentUser();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found"));

        // Patient can only view their own prescriptions.
        if (user.getRoles().contains(Roles.PATIENT)) {

            Patient currentPatient = patientRepository
                    .findByUser_Id(user.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Patient profile not found"));

            if (!currentPatient.getPatientId().equals(patientId)) {
                throw new UnAuthorisedException(
                        "You can only view your own prescriptions"
                );
            }
        }

        // Any registered doctor can view previous prescriptions.
        if (user.getRoles().contains(Roles.DOCTOR)) {
            getCurrentDoctor();
        }

        // Admin can view prescriptions of any patient.
        if (!user.getRoles().contains(Roles.ADMIN)
                && !user.getRoles().contains(Roles.PATIENT)
                && !user.getRoles().contains(Roles.DOCTOR)) {

            throw new UnAuthorisedException(
                    "You are not authorized to view prescriptions"
            );
        }

        return prescriptionRepository
                .findByMedicalRecord_Appointment_Patient_PatientIdAndIsDeletedFalse(
                        patient.getPatientId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponseDto> getAllPrescriptions() {

        User user = getCurrentUser();

        if (!user.getRoles().contains(Roles.ADMIN)) {
            throw new UnAuthorisedException(
                    "Only Admin can view all prescriptions"
            );
        }

        return prescriptionRepository.findByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void authorizePrescriptionView(
            User user,
            Prescription prescription) {

        if (user.getRoles().contains(Roles.ADMIN)) {
            return;
        }

        if (user.getRoles().contains(Roles.PATIENT)) {

            Patient patient = patientRepository.findByUser_Id(user.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Patient profile not found"));

            Long prescriptionPatientId = prescription.getMedicalRecord()
                    .getAppointment()
                    .getPatient()
                    .getPatientId();

            if (!patient.getPatientId().equals(prescriptionPatientId)) {
                throw new UnAuthorisedException(
                        "You are not authorized to view this prescription"
                );
            }

            return;
        }

        if (user.getRoles().contains(Roles.DOCTOR)) {

            // Any registered doctor has read-only access.
            getCurrentDoctor();
            return;
        }

        throw new UnAuthorisedException(
                "You are not authorized to view this prescription"
        );
    }

    private void addMedicines(
            Prescription prescription,
            List<PrescriptionMedicineRequestDto> requests) {

        List<PrescriptionMedicine> medicines = new ArrayList<>();

        for (PrescriptionMedicineRequestDto request : requests) {

            PrescriptionMedicine medicine =
                    new PrescriptionMedicine();

            medicine.setPrescription(prescription);
            medicine.setMedicineName(request.getMedicineName());
            medicine.setMedicineType(request.getMedicineType());
            medicine.setDosage(request.getDosage());

            medicine.setMorningDose(request.getMorningDose());
            medicine.setNoonDose(request.getNoonDose());
            medicine.setEveningDose(request.getEveningDose());

            medicine.setDoseUnit(request.getDoseUnit());
            medicine.setDurationDays(request.getDurationDays());
            medicine.setFoodInstruction(request.getFoodInstruction());

            medicine.setSpecialInstructions(
                    request.getSpecialInstructions()
            );

            medicines.add(medicine);
        }

        prescription.getMedicines().addAll(medicines);
    }

    private PrescriptionResponseDto mapToResponse(
            Prescription prescription) {

        MedicalRecord record = prescription.getMedicalRecord();
        Appointment appointment = record.getAppointment();

        PrescriptionResponseDto response =
                new PrescriptionResponseDto();

        response.setPrescriptionId(prescription.getPrescriptionId());
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

        response.setNotes(prescription.getNotes());

        response.setCreatedAt(prescription.getCreatedAt());
        response.setUpdatedAt(prescription.getUpdatedAt());

        List<PrescriptionMedicineResponseDto> medicineResponses =
                prescription.getMedicines()
                        .stream()
                        .map(medicine -> {

                            PrescriptionMedicineResponseDto dto =
                                    new PrescriptionMedicineResponseDto();

                            dto.setMedicineId(medicine.getMedicineId());
                            dto.setMedicineName(medicine.getMedicineName());
                            dto.setMedicineType(medicine.getMedicineType());
                            dto.setDosage(medicine.getDosage());

                            dto.setMorningDose(medicine.getMorningDose());
                            dto.setNoonDose(medicine.getNoonDose());
                            dto.setEveningDose(medicine.getEveningDose());

                            dto.setDoseUnit(medicine.getDoseUnit());
                            dto.setDurationDays(medicine.getDurationDays());
                            dto.setFoodInstruction(
                                    medicine.getFoodInstruction()
                            );

                            dto.setSpecialInstructions(
                                    medicine.getSpecialInstructions()
                            );

                            return dto;
                        })
                        .toList();

        response.setMedicines(medicineResponses);

        return response;
    }
}
