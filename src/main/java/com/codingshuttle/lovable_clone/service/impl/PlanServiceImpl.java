package com.codingshuttle.lovable_clone.service.impl;

import com.codingshuttle.lovable_clone.dto.subscription.PlanResponse;
import com.codingshuttle.lovable_clone.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
