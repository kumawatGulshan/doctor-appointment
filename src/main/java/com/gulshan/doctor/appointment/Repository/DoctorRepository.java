package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.Doctor;
import com.gulshan.doctor.appointment.Entity.Projections.DoctorDetailsProjection;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    @Query(value = """
            select 
            u.id as userId,
            u.created_at as createdAt,
            u.email as email,
            u.name as name,
            up.address_line as addressLine,
            up.city as city,
            up.pincode as pincode,
            up.country as country,
            up.date_of_birth as dateOfBirth,
            up.gender as gender,
            up.mob_number as mobNumber,
            up.state as state,
            d.avg_consultation_time as avgConsultationTime,
            d.consultation_fee as consultationFee,
            d.image_name as imageName,
            d.license_number as licenseNumber,
            d.practice_start_date as practiceStartDate,
            d.qualification_degree as qualificationDegree,
            s.specialization_name as specializationName
            from user_profile up
            join user u
            on u.id = up.user_id
            join doctor d
            on u.id = d.user_id
            join specialization s
            on s.specialization_id = d.specialization_specialization_id
            where up.deleted = false and u.id = :user_id and d.is_activated = true
            """,nativeQuery = true)
    Optional<DoctorDetailsProjection> findDoctorDetailsByUserId(@Param("user_id") Long userId);

    @Query(value = """
            select 
            u.id as userId,
            u.created_at as createdAt,
            u.email as email,
            u.name as name,
            up.address_line as addressLine,
            up.city as city,
            up.pincode as pincode,
            up.country as country,
            up.date_of_birth as dateOfBirth,
            up.gender as gender,
            up.mob_number as mobNumber,
            up.state as state,
            d.avg_consultation_time as avgConsultationTime,
            d.consultation_fee as consultationFee,
            d.image_name as imageName,
            d.license_number as licenseNumber,
            d.practice_start_date as practiceStartDate,
            d.qualification_degree as qualificationDegree,
            s.specialization_name as specializationName
            from user_profile up
            join user u
            on u.id = up.user_id
            join doctor d
            on u.id = d.user_id
            join specialization s
            on s.specialization_id = d.specialization_specialization_id
            where up.deleted = false and s.specialization_id = :specialization_id and d.is_activated = false
            """,nativeQuery = true)
    List<DoctorDetailsProjection> findAllBySpecialization_SpecializationId(@Param("specialization_id") Long specializationId);

    Optional<Doctor> findByUser_Id(Long userId);
}
