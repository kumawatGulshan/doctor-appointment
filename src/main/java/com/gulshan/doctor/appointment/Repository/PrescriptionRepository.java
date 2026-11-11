
package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByMedicalRecord_RecordIdAndIsDeletedFalse(
            Long recordId
    );

    Optional<Prescription> findByPrescriptionIdAndIsDeletedFalse(
            Long prescriptionId
    );

    List<Prescription>
    findByMedicalRecord_Appointment_Patient_PatientIdAndIsDeletedFalse(
            Long patientId
    );

    List<Prescription> findByIsDeletedFalse();

    boolean existsByMedicalRecord_RecordId(Long recordId);
}
