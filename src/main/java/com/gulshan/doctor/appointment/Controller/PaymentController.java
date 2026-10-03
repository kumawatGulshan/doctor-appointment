package com.gulshan.doctor.appointment.Controller;

import com.gulshan.doctor.appointment.ResposeDTO.ApiResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.RefundResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.PaymentServiceInterface;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@Slf4j
public class PaymentController {

    private final PaymentServiceInterface paymentServiceInterface;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/appointment/{appointmentId}")
    public ResponseEntity<Map<String,String>> initiatePayment(@PathVariable Long appointmentId) {
        String sessionUrl = paymentServiceInterface.initiatePayment(appointmentId);
        return ResponseEntity.ok(Map.of("sessionUrl",sessionUrl));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader){
            try{
                Event event = Webhook.constructEvent(payload,sigHeader,webhookSecret);
                paymentServiceInterface.capturePayment(event);
                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                log.error("Webhook error: {}", e.getMessage(), e);
                return ResponseEntity.badRequest().build();
            }
    }


    @PostMapping("/appointment/refund/{appointmentId}")
    public ResponseEntity<ApiResponseDto<RefundResponseDto>> refundPayment(@PathVariable Long appointmentId) {
        RefundResponseDto refundResponse = paymentServiceInterface.refundPayment(appointmentId);
        ApiResponseDto<RefundResponseDto> response = new ApiResponseDto<>("Refund processed successfully.", refundResponse);
        return ResponseEntity.ok(response);
    }

}
