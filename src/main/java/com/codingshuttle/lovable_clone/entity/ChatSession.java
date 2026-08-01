package com.codingshuttle.lovable_clone.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatSession {
    User user;

    Project project;
    String title;

    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;
}
