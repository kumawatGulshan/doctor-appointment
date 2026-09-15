package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.Gender;
import com.gulshan.doctor.appointment.Entity.Specialization;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DoctorResponseDto {

        // User
        private Long userId;
        private LocalDateTime createdAt;
        private String email;
        private String name;

        // UserProfile
        private String addressLine;
        private String city;
        private String country;
        private LocalDate dateOfBirth;
        private Gender gender;
        private String mobNumber;
        private String pincode;
        private String state;

        // Doctor
        private Integer avgConsultationTime;
        private BigDecimal consultationFee;
        private String imageName;
        private String licenseNumber;
        private LocalDate practiceStartDate;
        private String qualificationDegree;

        // Specialization
        private String specializationName;
    }

