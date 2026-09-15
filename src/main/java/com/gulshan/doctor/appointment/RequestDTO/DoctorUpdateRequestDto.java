package com.gulshan.doctor.appointment.RequestDTO;

import com.gulshan.doctor.appointment.Entity.Enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DoctorUpdateRequestDto {
        // User
        @NotNull(message = "User ID is required")
        private Long userId;

        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email")
        private String email;


        // UserProfile
        @NotBlank(message = "Mobile number is required")
        @Pattern(
                regexp = "^[6-9]\\d{9}$",
                message = "Invalid mobile number"
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


        // Doctor
        @NotBlank(message = "Qualification degree is required")
        private String qualificationDegree;

        @NotNull(message = "Specialization ID is required")
        private Long specializationId;

        @NotBlank(message = "License number is required")
        private String licenseNumber;

        @NotNull(message = "Consultation fee is required")
        @Positive(message = "Consultation fee must be greater than 0")
        private BigDecimal consultationFee;

        @NotNull(message = "Practice start date is required")
        @PastOrPresent(message = "Practice start date cannot be in the future")
        private LocalDate practiceStartDate;

        @NotBlank(message = "Image name is required")
        private String imageName;

        @NotNull(message = "Average consultation time is required (in minutes)")
        @Positive(message = "Average consultation time must be greater than 0")
        private Integer avgConsultationTime;
}
