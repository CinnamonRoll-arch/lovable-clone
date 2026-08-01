package com.codingshuttle.lovable_clone.service.impl;

import com.codingshuttle.lovable_clone.dto.subscription.SubscriptionResponse;
import com.codingshuttle.lovable_clone.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        return null;
    }

}
