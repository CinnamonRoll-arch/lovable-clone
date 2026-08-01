package com.codingshuttle.lovable_clone.service;

import com.codingshuttle.lovable_clone.dto.subscription.PlanResponse;

import java.util.List;

public interface PlanService {
    List<PlanResponse> getAllActivePlans();
}
