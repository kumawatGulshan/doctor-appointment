package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.Appointment;
import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Repository.AppointmentRepository;
import com.gulshan.doctor.appointment.Service.Interface.CheckoutServiceInterface;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class CheckoutServiceImpl implements CheckoutServiceInterface {

    private final AppointmentRepository appointmentRepository;


    @Override
    public String getCheckoutSession(Appointment appointment, String successUrl, String failureUrl) {
        log.info("Creating session for appointment with id : {}", appointment.getAppointmentId());

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{

            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();

            Customer customer = Customer.create(customerParams);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    .setUnitAmount(appointment.getDoctor().getConsultationFee().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(appointment.getDoctor().getUser().getName() +", "+appointment.getDoctor().getSpecialization().getSpecializationName())
                                                                    .setDescription("Appointment date : " + appointment.getAppointmentDate())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            appointment.setPaymentSessionId(session.getId());
            appointmentRepository.save(appointment);
            log.info("Session created successfully for appointment with id : {}", appointment.getAppointmentId());

            return session.getUrl();

        }catch (StripeException ex){
            log.error("Stripe error while creating checkout session: {}", ex.getMessage(), ex);
            throw new RuntimeException("Stripe error: " + ex.getMessage());
        }
    }
}
