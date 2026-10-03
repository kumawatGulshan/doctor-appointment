package com.gulshan.doctor.appointment.Service.Interface;

import com.gulshan.doctor.appointment.Entity.Appointment;

public interface CheckoutServiceInterface {
    String getCheckoutSession(Appointment appointment, String successUrl, String failureUrl);
}
