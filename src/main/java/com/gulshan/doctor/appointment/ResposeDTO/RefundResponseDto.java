package com.gulshan.doctor.appointment.ResposeDTO;


import com.gulshan.doctor.appointment.Entity.Enums.RefundStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundResponseDto {

    private Long refundId;
    private Long appointmentId;
    private Long paymentId;
    private String stripeRefundId;
    private BigDecimal refundAmount;
    private RefundStatus refundStatus;
    private LocalDateTime refundedTime;
}