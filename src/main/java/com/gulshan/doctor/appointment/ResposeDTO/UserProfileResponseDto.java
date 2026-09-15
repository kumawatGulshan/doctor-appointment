package com.gulshan.doctor.appointment.ResposeDTO;


import com.gulshan.doctor.appointment.Entity.Enums.Gender;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class UserProfileResponseDto {
    private long userId;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private String mobNumber;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String addressLine;
    private String city;
    private String pincode;
    private String state;
    private String country;
}
