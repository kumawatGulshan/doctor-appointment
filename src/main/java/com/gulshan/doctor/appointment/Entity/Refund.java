package com.gulshan.doctor.appointment.Entity;

import com.gulshan.doctor.appointment.Entity.Enums.PaymentMethod;
import com.gulshan.doctor.appointment.Entity.Enums.RefundStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund")
@Data
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "stripe_refund_id", unique = true)
    private String stripeRefundId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus refundStatus;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;


}
