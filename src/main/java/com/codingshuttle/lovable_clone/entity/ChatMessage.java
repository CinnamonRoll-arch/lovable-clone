package com.codingshuttle.lovable_clone.entity;

import com.codingshuttle.lovable_clone.enums.MessageRole;

import java.time.Instant;

public class ChatMessage {
    Long id;
    ChatSession chatSession;
    String content;
    String toolCalls; // JSON Array of tools called
    Integer tokenUsed;
    Instant createdAt;
    MessageRole role;
}
