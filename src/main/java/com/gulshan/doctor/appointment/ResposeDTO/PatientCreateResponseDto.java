package com.gulshan.doctor.appointment.ResposeDTO;


import com.gulshan.doctor.appointment.Entity.Enums.BloodGroup;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PatientCreateResponseDto {
    private BloodGroup bloodGroup;
    private String emergencyContactNumber;
}
