package com.gulshan.doctor.appointment.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

    private String qualificationDegree;

    @ManyToOne
    private Specialization specialization;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    private BigDecimal consultationFee;

    private LocalDate practiceStartDate;

    private String imageName;

    private Integer avgConsultationTime;

    @Column(nullable = false)
    private boolean isActivated = false;
}
