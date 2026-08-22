package com.cms.services;

import com.cms.entity.User;
import com.cms.entity.enums.Role;
import com.cms.exceptions.CustomException;
import com.cms.models.constants.ErrorCode;
import com.cms.models.dtos.requests.LoginRequest;
import com.cms.models.dtos.requests.RegisterRequest;
import com.cms.models.dtos.requests.VerifyTokenRequest;
import com.cms.models.dtos.responses.AuthResponse;
import com.cms.models.dtos.responses.UserResponse;
import com.cms.models.dtos.responses.VerifyTokenResponse;
import com.cms.repositories.UserRepository;
import com.cms.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "Tên đăng nhập đã tồn tại");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "Email đã được sử dụng");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .role(registerRequest.getRole() != null ? registerRequest.getRole() : Role.STUDENT)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .isActive(savedUser.getIsActive())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new CustomException(ErrorCode.BAD_CREDENTIALS, "Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Tên đăng nhập hoặc mật khẩu không chính xác"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "Tài khoản của bạn đã bị khóa");
        }

        String token = tokenProvider.generateToken(authentication);

        UserResponse userResponse = UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getJwtExpirationInMs() / 1000)
                .user(userResponse)
                .build();
    }

    @Override
    public VerifyTokenResponse verifyToken(VerifyTokenRequest request) {
        String token = request.getToken();

        // Loại bỏ prefix "Bearer " nếu client lỡ truyền kèm
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        boolean isValid = tokenProvider.validateToken(token);

        if (!isValid) {
            return VerifyTokenResponse.builder()
                    .valid(false)
                    .user(null)
                    .build();
        }

        // Lấy thông tin user từ token nếu token hợp lệ
        String username = tokenProvider.getUsernameFromJwt(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Người dùng không tồn tại"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            return VerifyTokenResponse.builder()
                    .valid(false)
                    .user(null)
                    .build();
        }

        UserResponse userResponse = UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();

        return VerifyTokenResponse.builder()
                .valid(true)
                .user(userResponse)
                .build();
    }

    @Override
    public UserResponse getCurrentUser() {
        // 1. Lấy username từ SecurityContext (đã được JwtAuthenticationFilter xác thực)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Tìm thông tin user trong DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Người dùng không tồn tại"));

        // 3. Trả về thông tin UserResponse
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }

}