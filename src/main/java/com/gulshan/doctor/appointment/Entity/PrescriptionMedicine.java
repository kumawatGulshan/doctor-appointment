
package com.gulshan.doctor.appointment.Entity;

import com.gulshan.doctor.appointment.Entity.Enums.DoseUnit;
import com.gulshan.doctor.appointment.Entity.Enums.FoodInstruction;
import com.gulshan.doctor.appointment.Entity.Enums.MedicineType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "prescription_medicine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionMedicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicineId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(nullable = false)
    private String medicineName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicineType medicineType;

    @Column(nullable = false)
    private String dosage;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal morningDose = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal noonDose = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal eveningDose = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DoseUnit doseUnit;

    @Column(nullable = false)
    private Integer durationDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodInstruction foodInstruction;

    @Column(columnDefinition = "TEXT")
    private String specialInstructions;
}
