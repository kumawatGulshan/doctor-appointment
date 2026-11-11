package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.Clinic;
import com.gulshan.doctor.appointment.Entity.Enums.ClinicApprovalStatus;
import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Exceptions.DuplicateResourceException;
import com.gulshan.doctor.appointment.Exceptions.InvalidApprovalStatusException;
import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Repository.ClinicRepository;
import com.gulshan.doctor.appointment.RequestDTO.ClinicCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.ClinicUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ClinicCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.ClinicResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.ClinicServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicServiceInterface {

    private final ClinicRepository clinicRepository;

    @Override
    @Transactional
    public ClinicCreateResponseDto createClinic(ClinicCreateRequestDto requestDto) {
        // Get the authenticated user from Spring Security.
        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (clinicRepository.existsByEmailAndIsDeleteFalse(requestDto.getEmail())) {
            throw new DuplicateResourceException(
                    "Clinic is already registered with this email"
            );
        }

        if (clinicRepository.existsByPhoneAndIsDeleteFalse(requestDto.getPhone())) {
            throw new DuplicateResourceException(
                    "Clinic is already registered with this phone number"
            );
        }

        if (clinicRepository.existsByAddressAndCityAndStateAndPincodeAndIsDeleteFalse(
                requestDto.getAddress(), requestDto.getCity(), requestDto.getState(), requestDto.getPincode())) {

            throw new DuplicateResourceException("A clinic is already registered at this address");
        }


        Clinic clinic = new Clinic();

        clinic.setName(requestDto.getName());
        clinic.setDescription(requestDto.getDescription());
        clinic.setAddress(requestDto.getAddress());
        clinic.setCity(requestDto.getCity());
        clinic.setState(requestDto.getState());
        clinic.setPincode(requestDto.getPincode());
        clinic.setPhone(requestDto.getPhone());
        clinic.setEmail(requestDto.getEmail());

        // The owner is taken from the logged-in user,
        // never from the request body.
        clinic.setOwner(owner);

        // Every new clinic requires Admin approval.
        clinic.setApprovalStatus(ClinicApprovalStatus.PENDING);

        Clinic savedClinic = clinicRepository.save(clinic);

        ClinicCreateResponseDto responseDto = new ClinicCreateResponseDto();

        responseDto.setId(savedClinic.getId());
        responseDto.setName(savedClinic.getName());
        responseDto.setCity(savedClinic.getCity());
        responseDto.setState(savedClinic.getState());
        responseDto.setApprovalStatus(savedClinic.getApprovalStatus());
        responseDto.setCreatedAt(savedClinic.getCreatedAt());
        responseDto.setEmail(savedClinic.getEmail());

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicResponseDto> getMyClinics() {
        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Clinic> clinics = clinicRepository.findByOwner_IdAndIsDeleteFalse(owner.getId());

        return clinics.stream().map(this::mapToClinicResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicResponseDto> getApprovedClinics() {

        List<Clinic> clinics = clinicRepository.findByApprovalStatusAndIsDeleteFalse(ClinicApprovalStatus.APPROVED);

        return clinics.stream().map(this::mapToClinicResponseDto).toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<ClinicResponseDto> getPendingClinics() {
        List<Clinic> clinics = clinicRepository.findByApprovalStatusAndIsDeleteFalse(ClinicApprovalStatus.PENDING);
        return clinics.stream().map(this::mapToClinicResponseDto).toList();
    }


    @Override
    public ClinicResponseDto updateApprovalStatus(Long clinicId, ClinicApprovalStatus approvalStatus) {

        Clinic clinic = clinicRepository.findByIdAndIsDeleteFalse(clinicId).orElseThrow(() ->
                new ResourceNotFoundException("Clinic not found with id: " + clinicId));

        if (approvalStatus == null || approvalStatus == ClinicApprovalStatus.PENDING) {
            throw new InvalidApprovalStatusException("Approval status must be APPROVED or REJECTED");
        }

        clinic.setApprovalStatus(approvalStatus);
        Clinic updatedClinic = clinicRepository.save(clinic);
        return mapToClinicResponseDto(updatedClinic);
    }


    @Override
    @Transactional
    public ClinicResponseDto updateClinic(Long clinicId, ClinicUpdateRequestDto requestDto) {

        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Clinic clinic = clinicRepository.findByIdAndIsDeleteFalse(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + clinicId));

        if (!clinic.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("You are not allowed to update this clinic");
        }

        if (clinicRepository.existsByEmailAndIdNotAndIsDeleteFalse(requestDto.getEmail(), clinicId)) {
            throw new DuplicateResourceException("Clinic is already registered with this email");
        }
        if (clinicRepository.existsByPhoneAndIdNotAndIsDeleteFalse(requestDto.getPhone(), clinicId)) {
            throw new DuplicateResourceException("Clinic is already registered with this phone number");
        }

        if (clinicRepository.existsByAddressAndCityAndStateAndPincodeAndIdNotAndIsDeleteFalse(
                requestDto.getAddress(), requestDto.getCity(), requestDto.getState(), requestDto.getPincode(),clinicId)) {

            throw new DuplicateResourceException("A clinic is already registered at this address");
        }

        clinic.setName(requestDto.getName());
        clinic.setDescription(requestDto.getDescription());
        clinic.setAddress(requestDto.getAddress());
        clinic.setCity(requestDto.getCity());
        clinic.setState(requestDto.getState());
        clinic.setPincode(requestDto.getPincode());
        clinic.setPhone(requestDto.getPhone());
        clinic.setEmail(requestDto.getEmail());

        // Admin must approve the updated clinic details again.
        clinic.setApprovalStatus(ClinicApprovalStatus.PENDING);

        Clinic updatedClinic = clinicRepository.save(clinic);

        return mapToClinicResponseDto(updatedClinic);
    }


    @Override
    @Transactional
    public String softDeleteClinic(Long clinicId) {

        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Clinic clinic = clinicRepository.findById(clinicId).orElseThrow(()
                -> new ResourceNotFoundException("Clinic not found with id: " + clinicId));

        if (!clinic.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this clinic");
        }
        if (clinic.isDelete()) {
            throw new ResourceNotFoundException("Clinic has already been deleted");
        }

        clinic.setDelete(true);
        clinicRepository.save(clinic);

        return "Clinic deleted successfully";
    }

    private ClinicResponseDto mapToClinicResponseDto(Clinic clinic) {

        ClinicResponseDto responseDto = new ClinicResponseDto();

        responseDto.setId(clinic.getId());
        responseDto.setName(clinic.getName());
        responseDto.setDescription(clinic.getDescription());
        responseDto.setAddress(clinic.getAddress());
        responseDto.setCity(clinic.getCity());
        responseDto.setState(clinic.getState());
        responseDto.setPincode(clinic.getPincode());
        responseDto.setPhone(clinic.getPhone());
        responseDto.setApprovalStatus(clinic.getApprovalStatus());
        responseDto.setCreatedAt(clinic.getCreatedAt());
        responseDto.setEmail(clinic.getEmail());

        return responseDto;
    }
}