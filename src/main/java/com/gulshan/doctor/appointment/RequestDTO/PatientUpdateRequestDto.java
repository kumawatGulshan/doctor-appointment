package com.gulshan.doctor.appointment.RequestDTO;


import com.gulshan.doctor.appointment.Entity.Enums.BloodGroup;
import com.gulshan.doctor.appointment.Entity.Enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class PatientUpdateRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    // User
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    // UserProfile
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
    private String pincode;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country name is required")
    private String country;

    // Patient

    private BloodGroup bloodGroup;


    @NotBlank(message = "Emergency contact number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid Indian emergency contact number"
    )
    private String emergencyContactNumber;
}
