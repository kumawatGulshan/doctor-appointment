package com.gulshan.doctor.appointment.Service.Implementation;


import com.gulshan.doctor.appointment.Repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentSchedulerService {
    private final AppointmentRepository appointmentRepository;

    @Scheduled(fixedDelay = 60000)
    public void expirePendingAppointments() {

        int count = appointmentRepository.cancelExpiredHolds(
                LocalDateTime.now()
        );

        if (count > 0) {
            log.info("Cancelled {} expired appointment holds.", count);
        }
    }
}
