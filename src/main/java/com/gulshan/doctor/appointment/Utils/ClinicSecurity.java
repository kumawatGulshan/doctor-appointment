
package com.gulshan.doctor.appointment.Utils;

import com.gulshan.doctor.appointment.Entity.Clinic;
import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Repository.ClinicRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("clinicSecurity")
public class ClinicSecurity {

    private final ClinicRepository clinicRepository;

    public ClinicSecurity(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public boolean isOwnerOfClinic(Long clinicId) {
        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Clinic clinic = clinicRepository.findByIdAndIsDeleteFalse(clinicId).orElseThrow(() ->
                        new ResourceNotFoundException("Clinic not found with id: " + clinicId));
        return clinic.getOwner().getId().equals(owner.getId());
    }
}
