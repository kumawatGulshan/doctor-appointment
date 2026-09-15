package com.gulshan.doctor.appointment.Entity.Projections;

import com.gulshan.doctor.appointment.Entity.Enums.BloodGroup;
import com.gulshan.doctor.appointment.Entity.Enums.Gender;
import com.gulshan.doctor.appointment.Entity.Specialization;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DoctorDetailsProjection {
    Long getUserId();
    String getName();
    String getEmail();
    LocalDateTime getCreatedAt();
    String getMobNumber();
    LocalDate getDateOfBirth();
    Gender getGender();
    String getAddressLine();
    String getPincode();
    String getCity();
    String getState();
    String getCountry();


    String getQualificationDegree();
    String getSpecializationName();
    String getLicenseNumber();
    BigDecimal getConsultationFee();
    LocalDate getPracticeStartDate();
    String getImageName();
    Integer getAvgConsultationTime();
}
