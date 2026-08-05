package com.codingshuttle.lovable_clone.service;

import com.codingshuttle.lovable_clone.dto.subscription.SubscriptionResponse;
import com.codingshuttle.lovable_clone.enums.SubscriptionStatus;

import java.time.Instant;


public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();

    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String subscriptionId);

    void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd);

    void markSubscriptionIdPastDue(String subId);

    boolean canCreateProject();
}
