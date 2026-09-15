package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.BloodGroup;
import com.gulshan.doctor.appointment.Entity.Enums.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
public class PatientUpdateResponseDto {
    private Long userId;

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

     private BloodGroup bloodGroup;
     private String emergencyContactNumber;
}
