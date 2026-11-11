
package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.Appointment;
import com.gulshan.doctor.appointment.Entity.Projections.ScheduledAppointmentProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    // Check whether appointment slot is occupied or temporarily held
    @Query(value = """
        SELECT COUNT(*) > 0
        FROM appointment a
        WHERE a.doctor_id = :doctorId
          AND a.appointment_date = :date
          AND a.appointment_time = :time
          AND (
            (a.appointment_status = 'CONFIRMED'
             AND a.payment_status = 'SUCCESS')
            OR
            (a.appointment_status = 'PENDING'
             AND a.payment_status = 'PENDING'
             AND a.hold_expires_at > :currentTime)
          )
        """, nativeQuery = true)
    boolean isSlotOccupiedOrHeld(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time,
            @Param("currentTime") LocalDateTime currentTime
    );


    // Find an active appointment for a particular doctor and slot
    @Query(value = """
        SELECT *
        FROM appointment a
        WHERE a.doctor_id = :doctorId
          AND a.appointment_date = :date
          AND a.appointment_time = :time
          AND (
              (
                  a.appointment_status = :confirmedStatus
                  AND a.payment_status = :successStatus
              )
              OR
              (
                  a.appointment_status = :pendingStatus
                  AND a.payment_status = :pendingPaymentStatus
                  AND a.hold_expires_at > :now
              )
          )
        """, nativeQuery = true)
    Optional<Appointment> findActiveAppointment(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time,
            @Param("confirmedStatus") String confirmedStatus,
            @Param("successStatus") String successStatus,
            @Param("pendingStatus") String pendingStatus,
            @Param("pendingPaymentStatus") String pendingPaymentStatus,
            @Param("now") LocalDateTime now
    );


    // Cancel expired appointment holds
    @Modifying
    @Transactional
    @Query("""
        UPDATE Appointment a
        SET a.appointmentStatus = 'CANCELLED',
            a.paymentStatus = 'CANCELLED',
            a.holdExpiresAt = NULL
        WHERE a.appointmentStatus = 'PENDING'
          AND a.paymentStatus = 'PENDING'
          AND a.holdExpiresAt <= :currentTime
        """)
    int cancelExpiredHolds(
            @Param("currentTime") LocalDateTime currentTime
    );


    // Fetch scheduled appointments for an independent doctor
    @Query(value = """
        SELECT
            ap.appointment_id AS appointmentId,
            ap.appointment_date AS appointmentDate,
            ap.appointment_time AS appointmentTime,
            ap.appointment_status AS appointmentStatus,
            ap.payment_status AS paymentStatus,
            ap.appointment_type AS appointmentType,
            u.name AS patientName,
            up.mob_number AS mobileNumber
        FROM appointment ap
        JOIN patient p
            ON ap.patient_id = p.patient_id
        JOIN user u
            ON u.id = p.user_id
        JOIN user_profile up
            ON up.user_id = u.id
        WHERE ap.doctor_id = :doctorId
          AND ap.appointment_date = :date
          AND ap.appointment_time >= :startTime
          AND ap.appointment_time < :endTime
          AND ap.appointment_status = 'CONFIRMED'
          AND ap.payment_status = 'SUCCESS'
        ORDER BY ap.appointment_time
        """, nativeQuery = true)
    List<ScheduledAppointmentProjection> findScheduledAppointments(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );


    // Find appointment using Stripe payment session ID
    Optional<Appointment> findByPaymentSessionId(String sessionId);


    // Fetch scheduled appointments for a doctor at a particular clinic
    @Query(value = """
        SELECT
            ap.appointment_id AS appointmentId,
            ap.appointment_date AS appointmentDate,
            ap.appointment_time AS appointmentTime,
            ap.appointment_status AS appointmentStatus,
            ap.payment_status AS paymentStatus,
            ap.appointment_type AS appointmentType,
            u.name AS patientName,
            up.mob_number AS mobNumber
        FROM appointment ap
        JOIN patient p
            ON ap.patient_id = p.patient_id
        JOIN user u
            ON u.id = p.user_id
        JOIN user_profile up
            ON up.user_id = u.id
        WHERE ap.doctor_id = :doctorId
          AND ap.clinic_id = :clinicId
          AND ap.appointment_date = :date
          AND ap.appointment_time >= :startTime
          AND ap.appointment_time < :endTime
          AND ap.appointment_status = 'CONFIRMED'
          AND ap.payment_status = 'SUCCESS'
        ORDER BY ap.appointment_time
        """, nativeQuery = true)
    List<ScheduledAppointmentProjection> findScheduledAppointmentsByClinic(
            @Param("doctorId") Long doctorId,
            @Param("clinicId") Long clinicId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
