package com.codingshuttle.lovable_clone.service.impl;

import com.codingshuttle.lovable_clone.dto.subscription.CheckoutRequest;
import com.codingshuttle.lovable_clone.dto.subscription.CheckoutResponse;
import com.codingshuttle.lovable_clone.dto.subscription.PortalResponse;
import com.codingshuttle.lovable_clone.entity.Plan;
import com.codingshuttle.lovable_clone.entity.User;
import com.codingshuttle.lovable_clone.exception.ResourceNotFoundException;
import com.codingshuttle.lovable_clone.repository.PlanRepository;
import com.codingshuttle.lovable_clone.repository.UserRepository;
import com.codingshuttle.lovable_clone.security.JwtUtils;
import com.codingshuttle.lovable_clone.service.PaymentProcessor;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripePaymentProcessor implements PaymentProcessor {

    private final PlanRepository planRepository;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Value("${client.url}")
    private String frontendUrl;

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request) {
       Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan",request.planId().toString()));

       Long userId = jwtUtils.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User",userId.toString()));

        var params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPrice(plan.getStripePriceId()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        SessionCreateParams.SubscriptionData.builder()
                                .setBillingMode(
                                        SessionCreateParams.SubscriptionData.BillingMode.builder()
                                                .setType(
                                                        SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE
                                                )
                                                .build()
                                )
                                .build()
                )
                .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel.html")
                .putMetadata("user_id", userId.toString())
                .putMetadata("plan_id", plan.getId().toString());


        try {

            String stripeCustomerId = user.getStripeCustomerId();

            if(stripeCustomerId == null || stripeCustomerId.isEmpty()){

                params.setCustomerEmail(user.getUsername());

            } else {
                params.setCustomer(stripeCustomerId);
            }


            Session session = Session.create(params.build());
            return new  CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PortalResponse openCustomerUrl(Long userId) {
        return null;
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {

    }
}
