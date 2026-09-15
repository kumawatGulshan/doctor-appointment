package com.gulshan.doctor.appointment.Controller;


import com.gulshan.doctor.appointment.RequestDTO.UserProfileRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.UserProfileUpdateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.UserProfileCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.UserProfileResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.UserProfileServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileServiceInterface userProfileServiceInterface;


    @GetMapping
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ApiResponseDto<List<UserProfileResponseDto>>> getAllUsersProfile(){
        List<UserProfileResponseDto> users = userProfileServiceInterface.getAllUsers();
        ApiResponseDto<List<UserProfileResponseDto>> response = new ApiResponseDto("User list found",users);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseDto<UserProfileResponseDto>> getUserProfileById(@PathVariable long userId){
       UserProfileResponseDto userProfileResponseDto =  userProfileServiceInterface.getUserById(userId);
       ApiResponseDto<UserProfileResponseDto> response = new ApiResponseDto<>("User found",userProfileResponseDto);
       return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping
    @Secured({"ROLE_ADMIN","ROLE_PATIENT","ROLE_DOCTOR"})
    public ResponseEntity<ApiResponseDto<UserProfileCreateResponseDto>> addUserDetails(@RequestBody @Valid UserProfileRequestDto userProfileRequestDto){
        UserProfileCreateResponseDto userProfileCreateResponseDto =  userProfileServiceInterface.addUserDetails(userProfileRequestDto);
        ApiResponseDto<UserProfileCreateResponseDto> response = new ApiResponseDto<>("User details added successfully",userProfileCreateResponseDto);
        return  new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PutMapping
    @PreAuthorize("@userProfileSecurity.isOwnerOfUserProfile(#userProfileUpdateRequestDto)")
    public ResponseEntity<ApiResponseDto<UserProfileResponseDto>> updateUserProfile(@RequestBody @Valid UserProfileUpdateRequestDto userProfileUpdateRequestDto){
        UserProfileResponseDto userProfileResponseDto = userProfileServiceInterface.updateUserProfile(userProfileUpdateRequestDto);
        ApiResponseDto<UserProfileResponseDto> response = new ApiResponseDto<>("Profile updated successfully.",userProfileResponseDto);
            return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("{userId}")
    @PreAuthorize("@userProfileSecurity.canUserDeleteThisProfile(#userId) OR hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserProfile(@PathVariable long userId){
        String response = userProfileServiceInterface.softDeleteUserProfile(userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
