package com.gulshan.doctor.appointment.Entity;



import com.gulshan.doctor.appointment.Entity.Enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userProfileId;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;


    private String mobNumber;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String addressLine;

    private String city;

    private String pincode;

    private String state;

    private String country;

    @Column(nullable = false)
    private boolean deleted = false;


}
