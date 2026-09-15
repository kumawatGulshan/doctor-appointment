package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.Patient;
import com.gulshan.doctor.appointment.Entity.Projections.PatientDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PatientRepository extends JpaRepository<Patient,Long> {

    Optional<Patient> findByUser_Id(Long aLong);

    @Query(value = """
                select 
                     u.id AS userId,
                            u.email AS email,
                            u.name AS name,
                            u.created_at AS createdAt,
                            up.address_line AS addressLine,
                            up.city AS city,
                            up.country AS country,
                            up.date_of_birth AS dateOfBirth,
                            up.gender AS gender,
                            up.mob_number AS mobNumber,
                            up.pincode AS pincode,
                            up.state AS state,
                            p.blood_group AS bloodGroup,
                            p.emergency_contact_number AS emergencyContactNumber
                from User u
                join user_profile up
                on u.id = up.user_id
                join patient p
                on u.id = p.user_id
                where up.deleted = false  AND u.id = :userId
    """, nativeQuery = true)

    Optional<PatientDetailsProjection> findPatientDetailsByUserId(@Param("userId") Long userId);




    @Query(value = """
                select DISTINCT
                    u.id AS userId,
                            u.email AS email,
                            u.name AS name,
                            u.created_at AS createdAt,
                            up.address_line AS addressLine,
                            up.city AS city,
                            up.country AS country,
                            up.date_of_birth AS dateOfBirth,
                            up.gender AS gender,
                            up.mob_number AS mobNumber,
                            up.pincode AS pincode,
                            up.state AS state,
                            p.blood_group AS bloodGroup,
                            p.emergency_contact_number AS emergencyContactNumber
                          from User u
                          join user_profile up
                          on u.id = up.user_id
                          join user_roles ur
                          on u.id = ur.user_id
                          join patient p
                          on u.id = p.user_id
                          where up.deleted = false and  ur.roles = 'PATIENT' 
    """, nativeQuery = true)
    List<PatientDetailsProjection> findAllPatientDetails();
}
