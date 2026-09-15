package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileCreateResponseDto {
    private long userId;
    private String mobNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String addressLine;
    private String city;
    private String pincode;
    private String state;
    private String country;
}
