package com.gulshan.doctor.appointment.Entity;


import com.gulshan.doctor.appointment.Entity.Enums.BloodGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private String emergencyContactNumber;

}
