package com.gulshan.doctor.appointment.Utils;

import com.gulshan.doctor.appointment.Entity.Doctor;
import com.gulshan.doctor.appointment.Entity.DoctorAvailability;
import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.RequestDTO.DoctorAvailabilityUpdateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.UserProfileUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.DoctorAvailabilityResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.DoctorAvailabilityServiceInterface;
import com.gulshan.doctor.appointment.Service.Interface.DoctorServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilitySecurity {

    public final DoctorAvailabilityServiceInterface doctorAvailabilityServiceInterface;
    public final DoctorServiceInterface doctorServiceInterface;

    public boolean canUserDeleteThisAvailability(long doctor_availability_id){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DoctorAvailability doctorAvailability = doctorAvailabilityServiceInterface.getAvailabilityEntityByDoctorAvailabilityId(doctor_availability_id);
        return (doctorAvailability.getDoctor().getUser().getId() == user.getId());
    }

    public boolean isOwnerOfAvailability(DoctorAvailabilityUpdateRequestDto doctorAvailabilityUpdateRequestDto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DoctorAvailability doctorAvailability = doctorAvailabilityServiceInterface
                .getAvailabilityEntityByDoctorAvailabilityId(doctorAvailabilityUpdateRequestDto.getDoctorAvailabilityId());
        return (doctorAvailability.getDoctor().getUser().getId() == user.getId());
    }

}
