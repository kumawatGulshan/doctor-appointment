
package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.DoctorAvailability;
import com.gulshan.doctor.appointment.Entity.Enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository
        extends JpaRepository<DoctorAvailability, Long> {

    // Fetch availability
    List<DoctorAvailability> findAllByDoctor_DoctorId(Long doctorId);

    List<DoctorAvailability> findAllByDoctor_DoctorIdAndClinic_Id(
            Long doctorId,
            Long clinicId
    );

    List<DoctorAvailability> findAllByDoctor_DoctorIdAndClinicIsNull(
            Long doctorId
    );

    // Global overlap check - CREATE
    // Checks all locations, including independent availability.
    boolean existsByDoctor_DoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime newEndTime,
            LocalTime newStartTime
    );

    // Global overlap check - UPDATE
    // Excludes the availability currently being updated.
    boolean existsByDoctor_DoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThanAndDoctorAvailabilityIdNot(
            Long doctorId,
            DayOfWeek dayOfWeek,
            LocalTime newEndTime,
            LocalTime newStartTime,
            Long doctorAvailabilityId
    );
}
