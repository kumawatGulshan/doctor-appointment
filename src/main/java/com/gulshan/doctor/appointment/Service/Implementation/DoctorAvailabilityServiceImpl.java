
package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.*;
import com.gulshan.doctor.appointment.Entity.Enums.DoctorClinicStatus;
import com.gulshan.doctor.appointment.Exceptions.DuplicateResourceException;
import com.gulshan.doctor.appointment.Exceptions.ResourceCreationException;
import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Exceptions.UnAuthorisedException;
import com.gulshan.doctor.appointment.Repository.ClinicRepository;
import com.gulshan.doctor.appointment.Repository.DoctorAvailabilityRepository;
import com.gulshan.doctor.appointment.Repository.DoctorClinicRepository;
import com.gulshan.doctor.appointment.RequestDTO.DoctorAvailabilityCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.DoctorAvailabilityUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorAvailabilityCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorAvailabilityResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.DoctorAvailabilityServiceInterface;
import com.gulshan.doctor.appointment.Service.Interface.DoctorServiceInterface;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityServiceImpl
        implements DoctorAvailabilityServiceInterface {

    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final DoctorServiceInterface doctorServiceInterface;
    private final ModelMapper modelMapper;
    private final ClinicRepository clinicRepository;
    private final DoctorClinicRepository doctorClinicRepository;


    // ADD AVAILABILITY
    @Override
    public DoctorAvailabilityCreateResponseDto addAvailability(
            DoctorAvailabilityCreateRequestDto dto) {

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Doctor doctor =
                doctorServiceInterface.getDoctorEntityByUserId(user.getId());

        // Manually create entity to prevent accidental ID mapping
        DoctorAvailability doctorAvailability = new DoctorAvailability();

        doctorAvailability.setDayOfWeek(dto.getDayOfWeek());
        doctorAvailability.setStartTime(dto.getStartTime());
        doctorAvailability.setEndTime(dto.getEndTime());
        doctorAvailability.setDoctor(doctor);

        // Clinic-specific availability
        if (dto.getClinicId() != null) {

            Clinic clinic = clinicRepository.findById(dto.getClinicId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Clinic not found"));

            boolean isAssociated =
                    doctorClinicRepository
                            .existsByDoctor_DoctorIdAndClinic_IdAndAssociationStatus(
                                    doctor.getDoctorId(),
                                    clinic.getId(),
                                    DoctorClinicStatus.ACTIVE);

            if (!isAssociated) {
                throw new UnAuthorisedException(
                        "You are not actively associated with this clinic");
            }

            doctorAvailability.setClinic(clinic);

        } else {
            // Independent availability
            doctorAvailability.setClinic(null);
        }

        // Validate time
        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time.");
        }

        // GLOBAL overlap check
        // Checks all clinics and independent availability.
        boolean isOverlapping =
                doctorAvailabilityRepository
                        .existsByDoctor_DoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
                                doctor.getDoctorId(),
                                dto.getDayOfWeek(),
                                dto.getEndTime(),
                                dto.getStartTime());

        if (isOverlapping) {
            throw new DuplicateResourceException(
                    "You already have an availability at another location during this time.");
        }

        try {

            DoctorAvailability savedDoctorAvailability =
                    doctorAvailabilityRepository.saveAndFlush(
                            doctorAvailability);

            DoctorAvailabilityCreateResponseDto response =
                    modelMapper.map(
                            savedDoctorAvailability,
                            DoctorAvailabilityCreateResponseDto.class);

            response.setClinicId(
                    savedDoctorAvailability.getClinic() == null
                            ? null
                            : savedDoctorAvailability.getClinic().getId());

            return response;

        } catch (DataIntegrityViolationException e) {

            String errorMessage =
                    e.getMostSpecificCause().getMessage();

            if (errorMessage != null &&
                    errorMessage.contains(
                            "uk_doctor_clinic_availability")) {

                throw new DuplicateResourceException(
                        "You can't add duplicate availability.");
            }

            throw new ResourceCreationException(
                    "Availability not saved: " + errorMessage);
        }
    }


    // GET ALL AVAILABILITY BY USER ID
    @Override
    public List<DoctorAvailabilityResponseDto> getAllAvailabilitiesByUserId(
            Long userId) {

        Doctor doctor =
                doctorServiceInterface.getDoctorEntityByUserId(userId);

        List<DoctorAvailability> list =
                doctorAvailabilityRepository
                        .findAllByDoctor_DoctorId(doctor.getDoctorId());

        return list.stream().map(availability -> {

            DoctorAvailabilityResponseDto response =
                    modelMapper.map(
                            availability,
                            DoctorAvailabilityResponseDto.class);

            response.setClinicId(
                    availability.getClinic() == null
                            ? null
                            : availability.getClinic().getId());

            return response;

        }).toList();
    }


    // GET AVAILABILITY BY ID
    @Override
    public DoctorAvailabilityResponseDto getAvailabilityByDoctorAvailabilityId(
            Long doctorAvailabilityId) {

        DoctorAvailability doctorAvailability =
                doctorAvailabilityRepository.findById(doctorAvailabilityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Availability not found."));

        DoctorAvailabilityResponseDto response =
                modelMapper.map(
                        doctorAvailability,
                        DoctorAvailabilityResponseDto.class);

        response.setClinicId(
                doctorAvailability.getClinic() == null
                        ? null
                        : doctorAvailability.getClinic().getId());

        return response;
    }


    // GET AVAILABILITY ENTITY BY ID
    @Override
    public DoctorAvailability getAvailabilityEntityByDoctorAvailabilityId(
            Long doctorAvailabilityId) {

        return doctorAvailabilityRepository.findById(doctorAvailabilityId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Availability not found."));
    }


    // DELETE AVAILABILITY
    @Override
    public String deleteDoctorAvailabilityById(long doctorAvailabilityId) {

        DoctorAvailability doctorAvailability =
                doctorAvailabilityRepository.findById(doctorAvailabilityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Availability not present in database."));

        doctorAvailabilityRepository.delete(doctorAvailability);

        return "Availability is deleted";
    }


    // UPDATE AVAILABILITY
    @Override
    public DoctorAvailabilityResponseDto updateDoctorAvailability(
            DoctorAvailabilityUpdateRequestDto dto) {

        DoctorAvailability doctorAvailability =
                doctorAvailabilityRepository.findById(
                                dto.getDoctorAvailabilityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Availability not present."));

        // Validate time
        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time.");
        }

        Doctor doctor = doctorAvailability.getDoctor();

        // GLOBAL overlap check
        // Excludes the current record but checks every location.
        boolean isOverlapping =
                doctorAvailabilityRepository
                        .existsByDoctor_DoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThanAndDoctorAvailabilityIdNot(
                                doctor.getDoctorId(),
                                dto.getDayOfWeek(),
                                dto.getEndTime(),
                                dto.getStartTime(),
                                dto.getDoctorAvailabilityId());

        if (isOverlapping) {
            throw new DuplicateResourceException(
                    "You cannot update availability because it overlaps with another availability at any location.");
        }

        // Update fields; clinic association remains unchanged.
        doctorAvailability.setEndTime(dto.getEndTime());
        doctorAvailability.setStartTime(dto.getStartTime());
        doctorAvailability.setDayOfWeek(dto.getDayOfWeek());

        try {

            DoctorAvailability updatedAvailability =
                    doctorAvailabilityRepository.saveAndFlush(
                            doctorAvailability);

            DoctorAvailabilityResponseDto response =
                    modelMapper.map(
                            updatedAvailability,
                            DoctorAvailabilityResponseDto.class);

            response.setClinicId(
                    updatedAvailability.getClinic() == null
                            ? null
                            : updatedAvailability.getClinic().getId());

            return response;

        } catch (DataIntegrityViolationException e) {

            throw new DuplicateResourceException(
                    "You can't update availability because the given availability already exists.");
        }
    }
}
