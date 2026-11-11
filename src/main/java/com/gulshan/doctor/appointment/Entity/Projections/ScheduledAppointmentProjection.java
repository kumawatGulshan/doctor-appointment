package com.gulshan.doctor.appointment.Entity.Projections;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ScheduledAppointmentProjection {

    Long getAppointmentId();

    LocalDate getAppointmentDate();

    LocalTime getAppointmentTime();

    String getAppointmentStatus();

    String getPaymentStatus();

    String getPatientName();

    String getMobNumber();

    String getAppointmentType();
}