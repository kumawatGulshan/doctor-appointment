package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability,Long> {
    List<DoctorAvailability> findAllByDoctor_DoctorId(Long doctorId);
}
