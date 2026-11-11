package com.gulshan.doctor.appointment.RequestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClinicUpdateRequestDto {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$",
            message = "Pincode must contain 6 digits")
    private String pincode;

    @NotBlank
    @Pattern(regexp = "^(\\+91[- ]?)?[6-9][0-9]{9}$",
            message = "Enter a valid Indian mobile number")
    private String phone;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;
}