package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.Clinic;
import com.gulshan.doctor.appointment.Entity.Enums.ClinicApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {


    List<Clinic> findByOwner_IdAndIsDeleteFalse(Long ownerId);


    List<Clinic> findByApprovalStatusAndIsDeleteFalse(ClinicApprovalStatus approvalStatus);

    boolean existsByPhoneAndIsDeleteFalse(String phone);

    boolean existsByEmailAndIsDeleteFalse(String email);

    boolean existsByAddressAndCityAndStateAndPincodeAndIsDeleteFalse
            (String address, String city, String state, String pincode);

    boolean existsByAddressAndCityAndStateAndPincodeAndIdNotAndIsDeleteFalse
            (String address, String city, String state, String pincode, Long id);
    Optional<Clinic> findByIdAndIsDeleteFalse(Long id);


    boolean existsByEmailAndIdNotAndIsDeleteFalse(String phone, Long id);
    boolean existsByPhoneAndIdNotAndIsDeleteFalse(String phone, Long id);

    boolean existsByIdAndOwner_IdAndIsDeleteFalse(Long clinicId, Long ownerId);
}