package com.codingshuttle.lovable_clone.service;

import com.codingshuttle.lovable_clone.dto.auth.AuthResponse;
import com.codingshuttle.lovable_clone.dto.auth.LoginRequest;
import com.codingshuttle.lovable_clone.dto.auth.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
