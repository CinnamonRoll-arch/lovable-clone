package com.codingshuttle.lovable_clone.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ChatSessionId {
    Long projectId;
    Long userId;
}