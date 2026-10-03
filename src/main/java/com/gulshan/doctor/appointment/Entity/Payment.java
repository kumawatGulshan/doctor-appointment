package com.gulshan.doctor.appointment.Entity;

import com.gulshan.doctor.appointment.Entity.Enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;


    //Transaction id
    @Column(name = "payment_intent_id", unique = true)
    private String paymentIntentId;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private PaymentStatus paymentStatus;
}
