package com.gulshan.doctor.appointment.RequestDTO;


import com.gulshan.doctor.appointment.Entity.Enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileUpdateRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid Indian mobile number"
    )
    private String mobNumber;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Address is required")
    private String addressLine;

    @NotBlank(message = "City name is required")
    private String city;

    @NotBlank(message = "Pincode is required")
    @Pattern(
            regexp = "^[1-9][0-9]{5}$",
            message = "Invalid pincode"
    )
    private String pincode;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country name is required")
    private String country;
}
