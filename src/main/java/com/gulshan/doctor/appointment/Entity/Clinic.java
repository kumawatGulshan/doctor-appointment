package com.gulshan.doctor.appointment.Entity;

import com.gulshan.doctor.appointment.Entity.Enums.ClinicApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clinics")
@Getter
@Setter
@NoArgsConstructor
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 6)
    private String pincode;

    @Column(length = 15,unique = true)
    private String phone;

    @Column(nullable = false,unique = true, length = 254)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClinicApprovalStatus approvalStatus =
            ClinicApprovalStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (approvalStatus == null) {
            approvalStatus = ClinicApprovalStatus.PENDING;
        }
    }

    @Column(nullable = false)
    private boolean isDelete = false;
}