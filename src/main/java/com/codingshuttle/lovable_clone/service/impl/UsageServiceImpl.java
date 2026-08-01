package com.codingshuttle.lovable_clone.service.impl;

import com.codingshuttle.lovable_clone.dto.subscription.PlanLimitsResponse;
import com.codingshuttle.lovable_clone.dto.subscription.UsageTodayResponse;
import com.codingshuttle.lovable_clone.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {
    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }

    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }
}
