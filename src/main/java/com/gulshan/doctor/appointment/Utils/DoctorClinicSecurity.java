package com.gulshan.doctor.appointment.Utils;

import com.gulshan.doctor.appointment.Entity.DoctorClinic;
import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Repository.ClinicRepository;
import com.gulshan.doctor.appointment.Repository.DoctorClinicRepository;
import com.gulshan.doctor.appointment.Repository.DoctorRepository;
import com.gulshan.doctor.appointment.Service.Implementation.DoctorClinicServiceImplementation;
import com.gulshan.doctor.appointment.Service.Interface.DoctorClinicServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("doctorClinicSecurity")
@RequiredArgsConstructor
public class DoctorClinicSecurity {

    private final ClinicRepository clinicRepository;
    private final DoctorClinicRepository doctorClinicRepository;

    public boolean isClinicOwner(Long clinicId) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return clinicRepository.existsByIdAndOwner_IdAndIsDeleteFalse(clinicId, loggedInUser.getId());
    }

    public boolean isOfferReceivedByLoggedInDoctor(Long associationId) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return doctorClinicRepository.existsByIdAndDoctor_User_Id(associationId, loggedInUser.getId());
    }

    public boolean isOwnerOfAssociation(Long associationId) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return doctorClinicRepository.findById(associationId).map(association ->
                        association.getClinic().getOwner().getId().equals(loggedInUser.getId())).orElse(false);
    }

    public boolean isDoctorOfAssociation(Long associationId) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return doctorClinicRepository.findById(associationId).map(association ->
                        association.getDoctor().getUser().getId().equals(loggedInUser.getId())).orElse(false);
    }
}