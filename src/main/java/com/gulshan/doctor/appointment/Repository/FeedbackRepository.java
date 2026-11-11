
package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    boolean existsByAppointment_AppointmentId(Long appointmentId);

    Optional<Feedback> findByFeedbackIdAndAppointment_Patient_PatientId(
            Long feedbackId, Long patientId);

    List<Feedback> findByAppointment_Doctor_DoctorIdOrderByCreatedAtDesc(
            Long doctorId);

    @Query("""
            SELECT AVG(f.rating)
            FROM Feedback f
            WHERE f.appointment.doctor.doctorId = :doctorId
            """)
    Double findAverageRatingByDoctorId(@Param("doctorId") Long doctorId);
}
