
package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    Optional<MedicalRecord> findByAppointment_AppointmentId(Long appointmentId);

    boolean existsByAppointment_AppointmentId(Long appointmentId);

    List<MedicalRecord> findByAppointment_Patient_PatientId(Long patientId);

    List<MedicalRecord> findByDoctor_DoctorId(Long doctorId);
}
