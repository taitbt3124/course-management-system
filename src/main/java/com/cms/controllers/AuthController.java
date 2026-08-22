package com.cms.controllers;

import com.cms.models.dtos.requests.LoginRequest;
import com.cms.models.dtos.requests.RegisterRequest;
import com.cms.models.dtos.requests.VerifyTokenRequest;
import com.cms.models.dtos.responses.ApiResponse;
import com.cms.models.dtos.responses.AuthResponse;
import com.cms.models.dtos.responses.UserResponse;
import com.cms.models.dtos.responses.VerifyTokenResponse;
import com.cms.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse userResponse = authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(200, "Đăng ký tài khoản thành công", userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(200, "Đăng nhập thành công", authResponse));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyTokenResponse>> verifyToken(@Valid @RequestBody VerifyTokenRequest request) {
        VerifyTokenResponse response = authService.verifyToken(request);
        return ResponseEntity.ok(ApiResponse.success(200, "Xác thực token thành công", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse userResponse = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(200, "Lấy thông tin người dùng thành công", userResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        authService.logout(bearerToken);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Đăng xuất thành công", null)
        );
    }
}