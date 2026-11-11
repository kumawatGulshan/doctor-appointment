package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.DoctorClinic;
import com.gulshan.doctor.appointment.Entity.Enums.DoctorClinicStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorClinicRepository
        extends JpaRepository<DoctorClinic, Long> {

    Optional<DoctorClinic> findByDoctor_DoctorIdAndClinic_Id(Long doctorId, Long clinicId);

    List<DoctorClinic> findByDoctor_DoctorIdAndAssociationStatus(Long doctorId, DoctorClinicStatus associationStatus);

    List<DoctorClinic> findByClinic_IdAndAssociationStatus(Long clinicId, DoctorClinicStatus associationStatus);

    boolean existsByIdAndDoctor_User_Id(Long associationId, Long userId);

    boolean existsByDoctor_DoctorIdAndClinic_IdAndAssociationStatus(Long doctorId, Long clinicId, DoctorClinicStatus associationStatus);

    Optional<DoctorClinic> findByDoctor_DoctorIdAndClinic_IdAndAssociationStatus(
            Long doctorId,
            Long clinicId,
            DoctorClinicStatus associationStatus
    );

}