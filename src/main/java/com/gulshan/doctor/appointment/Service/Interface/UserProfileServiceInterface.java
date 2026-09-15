package com.gulshan.doctor.appointment.Service.Interface;



import com.gulshan.doctor.appointment.RequestDTO.UserProfileRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.UserProfileUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.UserProfileCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.UserProfileResponseDto;

import java.util.List;

public interface UserProfileServiceInterface {
    UserProfileCreateResponseDto addUserDetails(UserProfileRequestDto userProfileRequestDto);

    List<UserProfileResponseDto> getAllUsers();

    UserProfileResponseDto getUserById(long userId);

    UserProfileResponseDto updateUserProfile(UserProfileUpdateRequestDto userProfileUpdateRequestDto);

    String softDeleteUserProfile(long userId);
}
