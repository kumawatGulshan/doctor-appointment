package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.ClinicApprovalStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ClinicResponseDto {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String phone;
    private String email;
    private ClinicApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
}