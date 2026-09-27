package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.Doctor;
import com.gulshan.doctor.appointment.Entity.DoctorAvailability;
import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Entity.UserProfile;
import com.gulshan.doctor.appointment.Exceptions.DuplicateResourceException;
import com.gulshan.doctor.appointment.Exceptions.ResourceCreationException;
import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Repository.DoctorAvailabilityRepository;
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
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityServiceInterface {

    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final DoctorServiceInterface doctorServiceInterface;
    private final ModelMapper modelMapper;

    @Override
    public DoctorAvailabilityCreateResponseDto addAvailability(DoctorAvailabilityCreateRequestDto dto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Doctor doctor = doctorServiceInterface.getDoctorEntityByUserId(user.getId());
        DoctorAvailability doctorAvailability = modelMapper.map(dto, DoctorAvailability.class);
        doctorAvailability.setDoctor(doctor);
        try{
            DoctorAvailability savedDoctorAvailability = doctorAvailabilityRepository.save(doctorAvailability);
            DoctorAvailabilityCreateResponseDto response = modelMapper.map(savedDoctorAvailability,DoctorAvailabilityCreateResponseDto.class);
            return response;
        } catch (DataIntegrityViolationException e) {
            if(e.getMostSpecificCause().getMessage().contains("uk_doctor_availability")){
                throw new DuplicateResourceException("You can't add duplicate availability.");
            }
            throw new ResourceCreationException("Availability not saved");
        }

    }

    @Override
    public List<DoctorAvailabilityResponseDto> getAllAvailabilitiesByUserId(Long userId) {
        Doctor doctor = doctorServiceInterface.getDoctorEntityByUserId(userId);
        List<DoctorAvailability> list = doctorAvailabilityRepository.findAllByDoctor_DoctorId(doctor.getDoctorId());
        List<DoctorAvailabilityResponseDto>  responseList = list.stream().
                map(l -> modelMapper.map(l,DoctorAvailabilityResponseDto.class)).toList();
        return responseList;
    }

    @Override
    public DoctorAvailabilityResponseDto getAvailabilityByDoctorAvailabilityId(Long doctorAvailabilityId) {
        DoctorAvailability doctorAvailability = doctorAvailabilityRepository.findById(doctorAvailabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found."));
        return modelMapper.map(doctorAvailability,DoctorAvailabilityResponseDto.class);
    }

    @Override
    public DoctorAvailability getAvailabilityEntityByDoctorAvailabilityId(Long doctorAvailabilityId) {
        return doctorAvailabilityRepository.findById(doctorAvailabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found."));
    }

    @Override
    public String deleteDoctorAvailabilityById(long doctorAvailabilityId) {
        DoctorAvailability doctorAvailability = doctorAvailabilityRepository.findById(doctorAvailabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not present in database."));
        doctorAvailabilityRepository.deleteById(doctorAvailabilityId);
        return "Availability is deleted";

    }

    @Override
    public DoctorAvailabilityResponseDto updateDoctorAvailability(DoctorAvailabilityUpdateRequestDto dto) {
        DoctorAvailability doctorAvailability = doctorAvailabilityRepository.findById(dto.getDoctorAvailabilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Availability not present."));
        doctorAvailability.setEndTime(dto.getEndTime());
        doctorAvailability.setStartTime(dto.getStartTime());
        doctorAvailability.setDayOfWeek(dto.getDayOfWeek());
        try {
            DoctorAvailability updatedAvailability = doctorAvailabilityRepository.save(doctorAvailability);
            return modelMapper.map(updatedAvailability, DoctorAvailabilityResponseDto.class);
        } catch (DataIntegrityViolationException e) {
                throw new DuplicateResourceException("You can't update availability because the given availability already exists.");
        }
    }
}
