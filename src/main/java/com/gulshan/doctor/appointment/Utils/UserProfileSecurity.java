package com.gulshan.doctor.appointment.Utils;


import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.RequestDTO.UserProfileUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.UserProfileResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.UserProfileServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileSecurity {

    private final UserProfileServiceInterface userProfileServiceInterface;

    public boolean isOwnerOfUserProfile(UserProfileUpdateRequestDto userProfileUpdateRequestDto){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (userProfileUpdateRequestDto.getUserId() == user.getId());
    }

    public boolean canUserDeleteThisProfile(long userId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfileResponseDto userProfileResponseDto = userProfileServiceInterface
                .getUserById(userId);
        return (userProfileResponseDto.getUserId() == user.getId());
    }
}
