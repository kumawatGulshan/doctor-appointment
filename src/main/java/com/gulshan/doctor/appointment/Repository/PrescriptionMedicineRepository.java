
package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.PrescriptionMedicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionMedicineRepository
        extends JpaRepository<PrescriptionMedicine, Long> {

    List<PrescriptionMedicine> findByPrescription_PrescriptionId(
            Long prescriptionId
    );
}
