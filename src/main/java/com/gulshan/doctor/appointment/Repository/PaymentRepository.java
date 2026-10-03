package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByAppointment_AppointmentId(Long appointmentId);
}
