package com.codingshuttle.lovable_clone.service.impl;

import com.codingshuttle.lovable_clone.dto.subscription.SubscriptionResponse;
import com.codingshuttle.lovable_clone.entity.Plan;
import com.codingshuttle.lovable_clone.entity.Subscription;
import com.codingshuttle.lovable_clone.entity.User;
import com.codingshuttle.lovable_clone.enums.SubscriptionStatus;
import com.codingshuttle.lovable_clone.exception.ResourceNotFoundException;
import com.codingshuttle.lovable_clone.mapper.SubscriptionMapper;
import com.codingshuttle.lovable_clone.repository.PlanRepository;
import com.codingshuttle.lovable_clone.repository.ProjectMemberRepository;
import com.codingshuttle.lovable_clone.repository.SubscriptionRepository;
import com.codingshuttle.lovable_clone.repository.UserRepository;
import com.codingshuttle.lovable_clone.security.JwtUtils;
import com.codingshuttle.lovable_clone.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final JwtUtils jwtUtils;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final ProjectMemberRepository projectMemberRepository;

    private final Integer FREE_TIER_PROJECTS_ALLOWED = 1;


    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId = jwtUtils.getCurrentUserId();

        Subscription subscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRAILING
        )).orElse(null);

        return subscriptionMapper.toSubscriptionResponse(subscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if (exists) return;

        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .build();
        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void updateSubscription(String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
           Subscription subscription = getSubscription(subscriptionId);
           boolean hasSubscriptionUpdated = false;

           if (status != null && status != subscription.getStatus()) {
               subscription.setStatus(status);
               hasSubscriptionUpdated = true;
           }
           if (periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())) {
               subscription.setCurrentPeriodStart(periodStart);
               hasSubscriptionUpdated = true;
           }
        if (periodEnd != null && !periodEnd.equals(subscription.getCurrentPeriodEnd())) {
            subscription.setCurrentPeriodEnd(periodEnd);
            hasSubscriptionUpdated = true;
        }
        if (cancelAtPeriodEnd != null && !cancelAtPeriodEnd.equals(subscription.getCurrentPeriodEnd())) {
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            hasSubscriptionUpdated = true;
        }
        if (planId != null && !planId.equals(subscription.getPlan().getId())) {
            Plan newPlan = getPlan(planId);
            subscription.setPlan(newPlan);
            hasSubscriptionUpdated = true;
        }

        if (hasSubscriptionUpdated) {
            log.debug("Subscription updated, {}", subscriptionId);
            subscriptionRepository.save(subscription);
        }

    }

    @Override
    public void cancelSubscription(String subscriptionId) {
        Subscription subscription = getSubscription(subscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);

    }

    @Override
    public void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd) {
          Subscription subscription = getSubscription(subId);

          Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
          subscription.setCurrentPeriodStart(newStart);
          subscription.setCurrentPeriodEnd(periodEnd);

          if (subscription.getStatus() == SubscriptionStatus.PAST_DUE  || subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
              subscription.setStatus(SubscriptionStatus.ACTIVE);
          }
          subscriptionRepository.save(subscription);

    }

    @Override
    public void markSubscriptionIdPastDue(String subId) {
        Subscription subscription = getSubscription(subId);
        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            log.debug("Subsciption is already past due, {}", subId);
            return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);
    }



    @Override
    public boolean canCreateProject() {
        SubscriptionResponse currentSubscription = getCurrentSubscription();
        Long userId = jwtUtils.getCurrentUserId();
        int countOfOwnedProjects = projectMemberRepository.countProjectOwnedByUser(userId);

        if (currentSubscription.plan() == null) {
            return countOfOwnedProjects < FREE_TIER_PROJECTS_ALLOWED;
        }
        return countOfOwnedProjects < currentSubscription.plan().maxProjects();
    }

    // UTILITY METHODS
    public User getUser(Long userId) {
         return userRepository.findById(userId)
                 .orElseThrow(() -> new ResourceNotFoundException("userId", userId.toString()));
    }
    public Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("planId", planId.toString()));
    }
    public Subscription getSubscription(String subId) {
        Subscription subscription = subscriptionRepository.findByStripeSubscriptionId(subId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscripton", subId));
        return subscription;
    }
}
