package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.ClinicApprovalStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ClinicCreateResponseDto {

    private Long id;
    private String name;
    private String city;
    private String state;
    private ClinicApprovalStatus approvalStatus;
    private LocalDateTime createdAt;

    private String email;
}