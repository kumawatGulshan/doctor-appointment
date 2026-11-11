
package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.Clinic;
import com.gulshan.doctor.appointment.Entity.Doctor;
import com.gulshan.doctor.appointment.Entity.DoctorClinic;
import com.gulshan.doctor.appointment.Entity.Enums.ClinicApprovalStatus;
import com.gulshan.doctor.appointment.Entity.Enums.DoctorClinicAction;
import com.gulshan.doctor.appointment.Entity.Enums.DoctorClinicStatus;
import com.gulshan.doctor.appointment.Entity.Enums.RemovedBy;
import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Exceptions.InvalidApprovalStatusException;
import com.gulshan.doctor.appointment.Exceptions.InvalidDoctorClinicActionException;
import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Repository.ClinicRepository;
import com.gulshan.doctor.appointment.Repository.DoctorClinicRepository;
import com.gulshan.doctor.appointment.Repository.DoctorRepository;
import com.gulshan.doctor.appointment.RequestDTO.DoctorClinicOfferRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorClinicResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.DoctorClinicServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorClinicServiceImplementation implements DoctorClinicServiceInterface {

    private final DoctorClinicRepository doctorClinicRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;

    // Clinic Owner sends an offer to a doctor.
    @Override
    public DoctorClinicResponseDto sendOffer(
            Long clinicId,
            Long doctorId,
            DoctorClinicOfferRequestDto requestDto
    ) {

        User owner = getLoggedInUser();

        Clinic clinic = clinicRepository.findById(clinicId).orElseThrow(() ->
                new ResourceNotFoundException("Clinic not found with id: " + clinicId));

        // Ensure the logged-in owner owns this clinic.
        if (clinic.getOwner() == null || !clinic.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("You are not allowed to manage this clinic");
        }

        // Only approved clinics can invite doctors.
        if (clinic.getApprovalStatus() != ClinicApprovalStatus.APPROVED) {
            throw new InvalidDoctorClinicActionException("Only approved clinics can send doctor offers");
        }

        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() ->
                new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        // Look for an existing doctor-clinic relationship.
        DoctorClinic association = doctorClinicRepository
                .findByDoctor_DoctorIdAndClinic_Id(doctorId, clinicId)
                .orElse(null);

        if (association != null) {

            if (association.getAssociationStatus() == DoctorClinicStatus.ACTIVE) {
                throw new InvalidDoctorClinicActionException("Doctor is already associated with this clinic");
            }

            if (association.getAssociationStatus() == DoctorClinicStatus.PENDING) {
                throw new InvalidDoctorClinicActionException("An offer is already pending for this doctor");
            }

            // Re-send an offer after a previous rejection.
            association.setConsultationFee(requestDto.getConsultationFee());

            association.setPhysicalConsultationEnabled(
                    requestDto.getPhysicalConsultationEnabled()
            );

            association.setVideoConsultationEnabled(
                    requestDto.getVideoConsultationEnabled()
            );

            association.setAssociationStatus(DoctorClinicStatus.PENDING);
            association.setRespondedAt(null);

        } else {

            association = new DoctorClinic();
            association.setDoctor(doctor);
            association.setClinic(clinic);
            association.setConsultationFee(requestDto.getConsultationFee());

            association.setPhysicalConsultationEnabled(
                    requestDto.getPhysicalConsultationEnabled()
            );

            association.setVideoConsultationEnabled(
                    requestDto.getVideoConsultationEnabled()
            );

            association.setAssociationStatus(DoctorClinicStatus.PENDING);
        }

        DoctorClinic savedAssociation = doctorClinicRepository.save(association);

        return mapToResponseDto(savedAssociation);
    }

    // Doctor views incoming pending offers.
    @Override
    @Transactional(readOnly = true)
    public List<DoctorClinicResponseDto> getPendingOffers() {

        User loggedInUser = getLoggedInUser();

        Doctor doctor = doctorRepository.findByUser_Id(loggedInUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor profile not found for logged-in user"
                        ));

        return doctorClinicRepository
                .findByDoctor_DoctorIdAndAssociationStatus(
                        doctor.getDoctorId(),
                        DoctorClinicStatus.PENDING
                )
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    // Doctor accepts OR rejects an offer using one method.
    @Override
    public DoctorClinicResponseDto respondToOffer(
            Long associationId,
            DoctorClinicAction action
    ) {

        if (action == null) {
            throw new IllegalArgumentException("Action must be ACCEPT or REJECT");
        }

        User loggedInUser = getLoggedInUser();

        Doctor doctor = doctorRepository.findByUser_Id(loggedInUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor profile not found for logged-in user"
                        ));

        DoctorClinic association = doctorClinicRepository.findById(associationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor-clinic offer not found with id: " + associationId
                        ));

        // Only the doctor who received the offer can respond.
        if (!association.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
            throw new AccessDeniedException(
                    "You are not allowed to respond to this offer"
            );
        }

        // Prevent responding to an offer more than once.
        if (association.getAssociationStatus() != DoctorClinicStatus.PENDING) {
            throw new InvalidDoctorClinicActionException(
                    "This offer has already been responded"
            );
        }

        if (action == DoctorClinicAction.ACCEPT) {

            // Do not activate an association if the clinic is no longer approved.
            if (association.getClinic().getApprovalStatus() != ClinicApprovalStatus.APPROVED) {
                throw new InvalidApprovalStatusException(
                        "This clinic is not currently approved"
                );
            }

            association.setAssociationStatus(DoctorClinicStatus.ACTIVE);

        } else {

            association.setAssociationStatus(DoctorClinicStatus.REJECTED);
        }

        association.setRespondedAt(LocalDateTime.now());

        DoctorClinic updatedAssociation = doctorClinicRepository.save(association);

        return mapToResponseDto(updatedAssociation);
    }

    // Clinic Owner views active doctors associated with their clinic.
    @Override
    @Transactional(readOnly = true)
    public List<DoctorClinicResponseDto> getClinicDoctors(Long clinicId) {

        User owner = getLoggedInUser();

        Clinic clinic = clinicRepository.findById(clinicId).orElseThrow(() ->
                new ResourceNotFoundException("Clinic not found with id: " + clinicId));

        if (clinic.getOwner() == null || !clinic.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to view doctors of this clinic"
            );
        }

        return doctorClinicRepository
                .findByClinic_IdAndAssociationStatus(
                        clinicId,
                        DoctorClinicStatus.ACTIVE
                )
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public String ownerRemoveDoctor(Long associationId) {

        DoctorClinic association = doctorClinicRepository.findById(associationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor-Clinic association not found"
                        ));

        User loggedInUser = getLoggedInUser();

        // Verify clinic ownership.
        if (!association.getClinic().getOwner().getId().equals(loggedInUser.getId())) {
            throw new AccessDeniedException(
                    "You are not authorized to remove this doctor"
            );
        }

        // Check association status.
        if (association.getAssociationStatus() != DoctorClinicStatus.ACTIVE) {
            throw new InvalidDoctorClinicActionException(
                    "Only active associations can be terminated"
            );
        }

        // Terminate association.
        association.setAssociationStatus(DoctorClinicStatus.TERMINATED);
        association.setRemovedBy(RemovedBy.CLINIC_OWNER);
        association.setRemovedAt(LocalDateTime.now());

        doctorClinicRepository.save(association);

        return "Doctor removed from clinic successfully";
    }

    @Override
    @Transactional
    public String doctorLeaveClinic(Long associationId) {

        DoctorClinic association = doctorClinicRepository.findById(associationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor-Clinic association not found"
                        ));

        User loggedInUser = getLoggedInUser();

        // Verify logged-in doctor.
        if (!association.getDoctor().getUser().getId().equals(loggedInUser.getId())) {
            throw new AccessDeniedException(
                    "You are not authorized to leave this clinic"
            );
        }

        // Check association status.
        if (association.getAssociationStatus() != DoctorClinicStatus.ACTIVE) {
            throw new InvalidDoctorClinicActionException(
                    "Only active associations can be terminated"
            );
        }

        // Terminate association.
        association.setAssociationStatus(DoctorClinicStatus.TERMINATED);
        association.setRemovedBy(RemovedBy.DOCTOR);
        association.setRemovedAt(LocalDateTime.now());

        doctorClinicRepository.save(association);

        return "Doctor removed from clinic successfully";
    }

    private User getLoggedInUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private DoctorClinicResponseDto mapToResponseDto(DoctorClinic association) {

        Doctor doctor = association.getDoctor();
        Clinic clinic = association.getClinic();

        return new DoctorClinicResponseDto(
                association.getId(),
                doctor.getDoctorId(),
                doctor.getUser().getId(),
                doctor.getUser().getName(),
                clinic.getId(),
                clinic.getName(),
                association.getConsultationFee(),
                association.getAssociationStatus(),
                association.getCreatedAt(),
                association.getRespondedAt(),
                association.getPhysicalConsultationEnabled(),
                association.getVideoConsultationEnabled()
        );
    }
}
