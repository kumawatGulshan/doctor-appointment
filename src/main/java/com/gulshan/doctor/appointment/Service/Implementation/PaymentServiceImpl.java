
package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.*;
import com.gulshan.doctor.appointment.Entity.Enums.AppointmentStatus;
import com.gulshan.doctor.appointment.Entity.Enums.PaymentMethod;
import com.gulshan.doctor.appointment.Entity.Enums.PaymentStatus;
import com.gulshan.doctor.appointment.Entity.Enums.RefundStatus;
import com.gulshan.doctor.appointment.Exceptions.*;
import com.gulshan.doctor.appointment.Repository.AppointmentRepository;
import com.gulshan.doctor.appointment.Repository.DoctorClinicRepository;
import com.gulshan.doctor.appointment.Repository.PaymentRepository;
import com.gulshan.doctor.appointment.Repository.RefundRepository;
import com.gulshan.doctor.appointment.ResposeDTO.RefundResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.PaymentServiceInterface;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentServiceInterface {

    private final AppointmentRepository appointmentRepository;
    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final CheckoutServiceImpl checkoutService;
    private final DoctorClinicRepository doctorClinicRepository;

    @Value("${frontEndUrl}")
    private String frontEndUrl;

    @Override
    public String initiatePayment(Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found with id : " + appointmentId
                        )
                );

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // Verify appointment ownership
        if (!user.getId().equals(appointment.getPatient().getUser().getId())) {
            throw new UnAuthorisedException(
                    "Appointment does not belong to this user with id : " + user.getId()
            );
        }

        // Prevent duplicate payment
        if (appointment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new DuplicateResourceException("Payment already completed");
        }

        // Payment is allowed only for pending appointments
        if (appointment.getAppointmentStatus() != AppointmentStatus.PENDING) {
            throw new InvalidAppointmentException(
                    "Payment can only be initiated for pending appointments"
            );
        }

        // Verify that the appointment hold has not expired
        if (appointment.getHoldExpiresAt() == null ||
                !appointment.getHoldExpiresAt().isAfter(LocalDateTime.now())) {

            throw new InvalidAppointmentException(
                    "Appointment payment hold has expired"
            );
        }

        appointment.setPaymentStatus(PaymentStatus.PENDING);
        appointmentRepository.save(appointment);

        return checkoutService.getCheckoutSession(
                appointment,
                frontEndUrl + "/payment/success",
                frontEndUrl + "/payment/failure"
        );
    }


    @Override
    public void capturePayment(Event event) {

        if (!"checkout.session.completed".equals(event.getType())) {
            log.info("Ignoring Stripe event type: {}", event.getType());
            return;
        }

        Session session = (Session) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() ->
                        new PaymentException(
                                "Unable to deserialize Stripe Checkout Session"
                        )
                );

        // Verify that Stripe has actually received the payment
        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {

            log.warn(
                    "Payment is not completed for Stripe session: {}. Payment status: {}",
                    session.getId(),
                    session.getPaymentStatus()
            );

            return;
        }

        // Find the appointment associated with this Stripe session
        String sessionId = session.getId();

        Appointment appointment = appointmentRepository
                .findByPaymentSessionId(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found for session ID: " + sessionId
                        )
                );

        Long appointmentId = appointment.getAppointmentId();

        // Prevent duplicate payment records if Stripe retries the webhook
        var existingPayment = paymentRepository
                .findByAppointment_AppointmentId(appointmentId);

        if (existingPayment.isPresent()) {

            log.info(
                    "Payment already exists for appointment ID: {}. Ignoring duplicate webhook.",
                    appointmentId
            );

            return;
        }

        // Get the actual amount charged by Stripe
        Long amountTotal = session.getAmountTotal();

        if (amountTotal == null || amountTotal <= 0) {
            throw new PaymentException(
                    "Invalid payment amount received from Stripe"
            );
        }

        // Stripe INR amounts are provided in paise
        BigDecimal amountPaid = BigDecimal.valueOf(amountTotal, 2);

        // Create and save payment record
        Payment payment = new Payment();

        payment.setAppointment(appointment);
        payment.setPaymentMethod(PaymentMethod.STRIPE);
        payment.setPaymentIntentId(session.getPaymentIntent());
        payment.setAmountPaid(amountPaid);

        // Do not swallow database exceptions.
        // If saving fails, the transaction will roll back.
        paymentRepository.save(payment);

        // Confirm appointment only after payment record is saved
        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
        appointment.setPaymentStatus(PaymentStatus.SUCCESS);
        appointment.setHoldExpiresAt(null);

        appointmentRepository.save(appointment);

        log.info(
                "Successfully booked appointment ID: {} after payment confirmation",
                appointmentId
        );
    }


    @Override
    public RefundResponseDto refundPayment(Long appointmentId) {

        // 1. Find appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found with id: " + appointmentId
                        )
                );

        // 2. Get logged-in user
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        // 3. Check appointment belongs to logged-in user
        if (!appointment.getPatient().getUser().getId().equals(user.getId())) {
            throw new UnAuthorisedException(
                    "You are not authorized to refund this appointment"
            );
        }

        // 4. Appointment must be cancelled
        if (appointment.getAppointmentStatus() != AppointmentStatus.CANCELLED) {
            throw new InvalidAppointmentException(
                    "Only cancelled appointments can be refunded"
            );
        }

        // 5. Payment must be refund pending
        if (appointment.getPaymentStatus() != PaymentStatus.REFUND_PENDING) {
            throw new InvalidAppointmentException(
                    "This appointment is not eligible for refund"
            );
        }

        // 6. Find payment
        Payment payment = paymentRepository
                .findByAppointment_AppointmentId(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for appointment id: " + appointmentId
                        )
                );

        // 7. Check payment method
        if (payment.getPaymentMethod() != PaymentMethod.STRIPE) {
            throw new InvalidPaymentException(
                    "Refund is not supported for this payment method"
            );
        }

        // 8. Check Stripe PaymentIntent ID
        if (payment.getPaymentIntentId() == null ||
                payment.getPaymentIntentId().isBlank()) {

            throw new InvalidPaymentException(
                    "Stripe payment intent ID not found"
            );
        }

        // 9. Check if refund already exists
        if (refundRepository.existsByPayment_PaymentId(payment.getPaymentId())) {
            throw new DuplicateResourceException(
                    "Refund has already been initiated for this payment"
            );
        }

        // 10. Create refund record
        Refund refund = new Refund();

        refund.setPayment(payment);
        refund.setRefundAmount(payment.getAmountPaid());
        refund.setRefundStatus(RefundStatus.PENDING);

        refund = refundRepository.save(refund);

        try {

            // 11. Create Stripe refund
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getPaymentIntentId())
                    .setAmount(
                            payment.getAmountPaid()
                                    .multiply(BigDecimal.valueOf(100))
                                    .longValue()
                    )
                    .build();

            com.stripe.model.Refund stripeRefund =
                    com.stripe.model.Refund.create(params);

            // 12. Save Stripe refund information
            refund.setStripeRefundId(stripeRefund.getId());

            // 13. Check Stripe refund status
            if ("succeeded".equalsIgnoreCase(stripeRefund.getStatus())) {

                refund.setRefundStatus(RefundStatus.SUCCESS);
                refund.setCreatedTime(LocalDateTime.now());

            } else {

                refund.setRefundStatus(RefundStatus.FAILED);
                refundRepository.save(refund);

                throw new PaymentException(
                        "Stripe refund was not successful"
                );
            }

            // 14. Save updated refund
            Refund savedRefund = refundRepository.save(refund);

            appointment.setPaymentStatus(PaymentStatus.REFUND);
            appointmentRepository.save(appointment);

            // 15. Create response
            RefundResponseDto response = new RefundResponseDto();

            response.setRefundId(savedRefund.getRefundId());
            response.setAppointmentId(appointmentId);
            response.setPaymentId(payment.getPaymentId());
            response.setStripeRefundId(savedRefund.getStripeRefundId());
            response.setRefundAmount(savedRefund.getRefundAmount());
            response.setRefundStatus(savedRefund.getRefundStatus());
            response.setRefundedTime(savedRefund.getCreatedTime());

            return response;

        } catch (StripeException e) {

            refund.setRefundStatus(RefundStatus.FAILED);
            refundRepository.save(refund);

            throw new PaymentException(
                    "Refund failed: " + e.getMessage()
            );
        }
    }
}
