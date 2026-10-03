package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund,Long> {
    boolean existsByPayment_PaymentId(Long paymentId);
}
