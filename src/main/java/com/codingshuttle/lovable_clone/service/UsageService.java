package com.codingshuttle.lovable_clone.service;

import com.codingshuttle.lovable_clone.dto.subscription.PlanLimitsResponse;
import com.codingshuttle.lovable_clone.dto.subscription.UsageTodayResponse;

public interface UsageService {

    UsageTodayResponse getTodayUsageOfUser(Long userId);

    PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
