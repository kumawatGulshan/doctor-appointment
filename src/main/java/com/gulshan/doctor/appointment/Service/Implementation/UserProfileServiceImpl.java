package com.gulshan.doctor.appointment.Service.Implementation;


import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Entity.UserProfile;
import com.gulshan.doctor.appointment.Exceptions.DuplicateResourceException;
import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Repository.UserProfileRepository;
import com.gulshan.doctor.appointment.RequestDTO.UserProfileRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.UserProfileUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.UserProfileCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.UserProfileResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.UserProfileServiceInterface;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileServiceInterface {

    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserProfileCreateResponseDto addUserDetails(UserProfileRequestDto userProfileRequestDto) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfile userProfile =  modelMapper.map(userProfileRequestDto, UserProfile.class);
        userProfile.setUser(user);

        userProfileRepository.findByUser_IdAndDeletedFalse(user.getId())
                .ifPresent(profile -> {
                    throw new DuplicateResourceException(
                            "User details already exist"
                    );
                });

        UserProfile savedUserprofile = userProfileRepository.save(userProfile);


        UserProfileCreateResponseDto userProfileCreateResponseDto = modelMapper.map(savedUserprofile,UserProfileCreateResponseDto.class);
        return userProfileCreateResponseDto;
    }

    @Override
    public List<UserProfileResponseDto> getAllUsers() {
       List<UserProfile> users = userProfileRepository.findByDeletedFalse();
       List<UserProfileResponseDto> response = users.stream()
               .map(userProfile ->{
                   UserProfileResponseDto dto = modelMapper.map(userProfile, UserProfileResponseDto.class);
                      User user =  userProfile.getUser();
                      dto.setUserId(user.getId());
                      dto.setName(user.getName());
                      dto.setEmail(user.getEmail());
                      dto.setCreatedAt(user.getCreatedAt());
                      return dto;
               })

               .toList();
       return response;
    }

    @Override
    public UserProfileResponseDto getUserById(long userId) {
        UserProfile savedUser =  userProfileRepository.findByUser_IdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User details not found."));
        UserProfileResponseDto response = modelMapper.map(savedUser,UserProfileResponseDto.class);
        User user = savedUser.getUser();
        response.setCreatedAt(user.getCreatedAt());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setUserId(user.getId());
        return  response;
    }

    @Override
    public UserProfileResponseDto updateUserProfile(UserProfileUpdateRequestDto userProfileUpdateRequestDto) {
        UserProfile savedUserProfile = userProfileRepository.findByUser_IdAndDeletedFalse(userProfileUpdateRequestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User profile not exist."));

        savedUserProfile.setMobNumber(userProfileUpdateRequestDto.getMobNumber());
        savedUserProfile.setDateOfBirth(userProfileUpdateRequestDto.getDateOfBirth());
        savedUserProfile.setGender(userProfileUpdateRequestDto.getGender());
        savedUserProfile.setAddressLine(userProfileUpdateRequestDto.getAddressLine());
        savedUserProfile.setCity(userProfileUpdateRequestDto.getCity());
        savedUserProfile.setPincode(userProfileUpdateRequestDto.getPincode());
        savedUserProfile.setState(userProfileUpdateRequestDto.getState());
        savedUserProfile.setCountry(userProfileUpdateRequestDto.getCountry());
//        savedUserProfile.setUser(savedUserProfile.getUser());
        UserProfile updatedUserProfile = userProfileRepository.save(savedUserProfile);
        UserProfileResponseDto userProfileResponseDto =  modelMapper.map(updatedUserProfile,UserProfileResponseDto.class);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userProfileResponseDto.setEmail(user.getEmail());
        userProfileResponseDto.setName(user.getName());
        userProfileResponseDto.setCreatedAt(user.getCreatedAt());
        return userProfileResponseDto;
    }

    @Override
    public String softDeleteUserProfile(long userId) {
        UserProfile userProfile = userProfileRepository.findByUser_IdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not present in database."));
        if(!userProfile.isDeleted()){
            userProfile.setDeleted(true);
        }else{
            return "User already deleted.";
        }
        userProfileRepository.save(userProfile);
        return "User deleted successfully.";

    }
}
