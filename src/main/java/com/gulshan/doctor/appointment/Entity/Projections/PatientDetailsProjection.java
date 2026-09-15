package com.gulshan.doctor.appointment.Entity.Projections;

import com.gulshan.doctor.appointment.Entity.Enums.BloodGroup;
import com.gulshan.doctor.appointment.Entity.Enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PatientDetailsProjection {
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
    BloodGroup getBloodGroup();
    String getEmergencyContactNumber();

}
