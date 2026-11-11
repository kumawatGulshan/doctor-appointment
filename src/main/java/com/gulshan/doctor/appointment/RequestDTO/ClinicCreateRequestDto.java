package com.gulshan.doctor.appointment.RequestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClinicCreateRequestDto {

    @NotBlank(message = "Clinic name is required")
    @Size(max = 150, message = "Clinic name cannot exceed 150 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100)
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(
            regexp = "^[0-9]{6}$",
            message = "Pincode must contain exactly 6 digits"
    )
    private String pincode;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(\\+91[ -]?)?[6-9][0-9]{9}$",
            message = "Enter a valid Indian mobile number"
    )

    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid clinic email")
    @Size(max = 254, message = "Email cannot exceed 254 characters")
    private String email;

}