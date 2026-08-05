package com.codingshuttle.lovable_clone.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chat_sessions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSession {

    @EmbeddedId
    ChatSessionId chatSessionId;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id",nullable = false,updatable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("projectId")
    @JoinColumn(name = "project_id",nullable = false,updatable = false)
    Project project;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;
}
