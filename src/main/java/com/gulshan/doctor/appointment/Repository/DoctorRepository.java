package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.Doctor;
import com.gulshan.doctor.appointment.Entity.Projections.DoctorDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query(value = """
            SELECT
                u.id AS userId,
                u.created_at AS createdAt,
                u.email AS email,
                u.name AS name,

                up.address_line AS addressLine,
                up.city AS city,
                up.pincode AS pincode,
                up.country AS country,
                up.date_of_birth AS dateOfBirth,
                up.gender AS gender,
                up.mob_number AS mobNumber,
                up.state AS state,

                d.avg_consultation_time AS avgConsultationTime,
                d.consultation_fee AS consultationFee,
                d.image_name AS imageName,
                d.license_number AS licenseNumber,
                d.practice_start_date AS practiceStartDate,
                d.qualification_degree AS qualificationDegree,

                d.physical_consultation_enabled AS physicalConsultationEnabled,
                d.video_consultation_enabled AS videoConsultationEnabled,

                s.specialization_name AS specializationName

            FROM user_profile up
            JOIN user u
                ON u.id = up.user_id
            JOIN doctor d
                ON u.id = d.user_id
            JOIN specialization s
                ON s.specialization_id = d.specialization_specialization_id

            WHERE up.deleted = false
                AND u.id = :user_id
                AND d.is_activated = true
            """, nativeQuery = true)
    Optional<DoctorDetailsProjection> findDoctorDetailsByUserId(
            @Param("user_id") Long userId
    );


    @Query(value = """
            SELECT
                u.id AS userId,
                u.created_at AS createdAt,
                u.email AS email,
                u.name AS name,

                up.address_line AS addressLine,
                up.city AS city,
                up.pincode AS pincode,
                up.country AS country,
                up.date_of_birth AS dateOfBirth,
                up.gender AS gender,
                up.mob_number AS mobNumber,
                up.state AS state,

                d.avg_consultation_time AS avgConsultationTime,
                d.consultation_fee AS consultationFee,
                d.image_name AS imageName,
                d.license_number AS licenseNumber,
                d.practice_start_date AS practiceStartDate,
                d.qualification_degree AS qualificationDegree,

                d.physical_consultation_enabled AS physicalConsultationEnabled,
                d.video_consultation_enabled AS videoConsultationEnabled,

                s.specialization_name AS specializationName

            FROM user_profile up
            JOIN user u
                ON u.id = up.user_id
            JOIN doctor d
                ON u.id = d.user_id
            JOIN specialization s
                ON s.specialization_id = d.specialization_specialization_id

            WHERE up.deleted = false
                AND s.specialization_id = :specialization_id
                AND d.is_activated = false
            """, nativeQuery = true)
    List<DoctorDetailsProjection> findAllBySpecialization_SpecializationId(
            @Param("specialization_id") Long specializationId
    );


    Optional<Doctor> findByUser_Id(Long userId);
}