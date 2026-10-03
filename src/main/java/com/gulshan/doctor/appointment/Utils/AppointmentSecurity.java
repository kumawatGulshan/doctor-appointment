package com.gulshan.doctor.appointment.Utils;

import com.gulshan.doctor.appointment.Entity.DoctorAvailability;
import com.gulshan.doctor.appointment.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentSecurity {


    public boolean canCurrentUserAccess(long userId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (userId == user.getId());
    }
}
