package com.gulshan.doctor.appointment.Entity;

import com.gulshan.doctor.appointment.Entity.Enums.DoctorClinicStatus;
import com.gulshan.doctor.appointment.Entity.Enums.RemovedBy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "doctor_clinics",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"doctor_id", "clinic_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DoctorClinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DoctorClinicStatus associationStatus =
            DoctorClinicStatus.PENDING;

    private LocalDateTime createdAt;

    private LocalDateTime respondedAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Enumerated(EnumType.STRING)
    private RemovedBy removedBy;

    private LocalDateTime removedAt;


    @Column(nullable = false)
    private Boolean physicalConsultationEnabled = true;

    @Column(nullable = false)
    private Boolean videoConsultationEnabled = false;
}