package com.hungrezy.foodordering.service;

import com.hungrezy.foodordering.dto.request.LoginRequest;
import com.hungrezy.foodordering.dto.request.RefreshTokenRequest;
import com.hungrezy.foodordering.dto.request.RegisterRequest;
import com.hungrezy.foodordering.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse register(String role, RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
}
