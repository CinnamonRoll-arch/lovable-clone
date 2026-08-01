package com.codingshuttle.lovable_clone.entity;

import java.time.Instant;

public class UsageLog {
    Long id;
    User user;
    Project project;

    String action;

    Integer tokenUsed;
    Integer durationMs;

    String metadata; // JSON of model_used , prompt_used

    Instant createdAt;

}
