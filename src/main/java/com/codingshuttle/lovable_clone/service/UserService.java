package com.codingshuttle.lovable_clone.service;

import com.codingshuttle.lovable_clone.dto.auth.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(Long userId);
}
