package com.gulshan.doctor.appointment.Service.Interface;

import com.gulshan.doctor.appointment.ResposeDTO.RefundResponseDto;
import com.stripe.model.Event;

public interface PaymentServiceInterface {
    String initiatePayment(Long appointmentId);

    void capturePayment(Event event);

    RefundResponseDto refundPayment(Long appointmentId);
}
