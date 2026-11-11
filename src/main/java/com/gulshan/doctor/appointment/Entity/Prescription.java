
package com.gulshan.doctor.appointment.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prescriptionId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medical_record_id", nullable = false, unique = true)
    private MedicalRecord medicalRecord;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(
            mappedBy = "prescription",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PrescriptionMedicine> medicines = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    private LocalDateTime deletedAt;
}
