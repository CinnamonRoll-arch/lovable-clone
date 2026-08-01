package com.codingshuttle.lovable_clone.dto.auth;

public record UserProfileResponse(
        Long id,
        String username,
        String name
) {
}
