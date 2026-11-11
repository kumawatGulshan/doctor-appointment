package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.VideoConsultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoConsultationRepository
        extends JpaRepository<VideoConsultation, Long> {

    Optional<VideoConsultation> findByAppointment_AppointmentId(
            Long appointmentId
    );

    boolean existsByAppointment_AppointmentId(Long appointmentId);
}