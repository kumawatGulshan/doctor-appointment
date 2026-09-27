package com.gulshan.doctor.appointment.ResposeDTO;

import com.gulshan.doctor.appointment.Entity.Enums.DayOfWeek;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Data
@Getter
@Setter
public class DoctorAvailabilityCreateResponseDto {

        private Long doctorAvailabilityId;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;

}
