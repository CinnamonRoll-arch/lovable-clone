package com.codingshuttle.lovable_clone.service;

import com.codingshuttle.lovable_clone.dto.subscription.SubscriptionResponse;


public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(Long userId);
}
