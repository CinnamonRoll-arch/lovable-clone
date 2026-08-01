package com.codingshuttle.lovable_clone.service;

import com.codingshuttle.lovable_clone.dto.subscription.CheckoutRequest;
import com.codingshuttle.lovable_clone.dto.subscription.CheckoutResponse;
import com.codingshuttle.lovable_clone.dto.subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;


public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerUrl(Long userId);

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
