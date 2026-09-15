package com.gulshan.doctor.appointment.RequestDTO;


import com.gulshan.doctor.appointment.Entity.Enums.BloodGroup;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class PatientCreateRequestDto {
    private BloodGroup bloodGroup;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid Indian mobile number"
    )
    private String emergencyContactNumber;
}
