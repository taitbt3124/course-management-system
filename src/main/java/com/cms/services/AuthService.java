package com.cms.services;

import com.cms.models.dtos.requests.LoginRequest;
import com.cms.models.dtos.requests.RegisterRequest;
import com.cms.models.dtos.requests.VerifyTokenRequest;
import com.cms.models.dtos.responses.AuthResponse;
import com.cms.models.dtos.responses.UserResponse;
import com.cms.models.dtos.responses.VerifyTokenResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    UserResponse register(RegisterRequest registerRequest);
    VerifyTokenResponse verifyToken(VerifyTokenRequest request);
    UserResponse getCurrentUser();
    void logout(String bearerToken);

}