package com.codingshuttle.lovable_clone.dto.subscription;

public record PlanResponse(
        Long id,
        String name,

        Integer maxProjects,
        Integer maxTokensPerDay,
        Integer maxPreviews, // max no of prev allowed per plan
        Boolean unlimitedAi,
        String price // unlimited access to LLM ignore maxTokens if true

) {
}
