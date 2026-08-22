package com.cms.controllers;

import com.cms.entity.enums.Role;
import com.cms.models.dtos.requests.CreateUserRequest;
import com.cms.models.dtos.requests.UpdateStatusRequest;
import com.cms.models.dtos.requests.UpdateUserRequest;
import com.cms.models.dtos.responses.ApiResponse;
import com.cms.models.dtos.responses.UserResponse;
import com.cms.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cms.models.dtos.requests.UpdateRoleRequest;
import com.cms.models.dtos.requests.UpdateStatusRequest;
import com.cms.models.dtos.requests.UpdateUserRequest;
import com.cms.models.dtos.requests.UpdateUserRequest;
import com.cms.models.dtos.requests.ChangePasswordRequest;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(value = "role", required = false) Role role,
            @RequestParam(value = "status", required = false) Boolean status) {

        List<UserResponse> users = userService.getAllUsers(role, status);
        return ResponseEntity.ok(
                ApiResponse.success(200, "Lấy danh sách người dùng thành công", users)
        );
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("user_id") Long userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(200, "Lấy thông tin người dùng thành công", user));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success(201, "Tạo tài khoản người dùng thành công", response));
    }
    @PutMapping("/{user_id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable("user_id") Long userId,
            @Valid @RequestBody UpdateRoleRequest request) {

        UserResponse response = userService.updateUserRole(userId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Cập nhật vai trò người dùng thành công", response));
    }


    @PutMapping("/{user_id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable("user_id") Long userId,
            @Valid @RequestBody UpdateStatusRequest request) {

        UserResponse response = userService.updateUserStatus(userId, request);
        String message = Boolean.TRUE.equals(request.getStatus()) ? "Kích hoạt tài khoản thành công" : "Vô hiệu hóa tài khoản thành công";
        return ResponseEntity.ok(ApiResponse.success(200, message, response));
    }

    @DeleteMapping("/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(200, "Xóa người dùng thành công", null));
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable("user_id") Long userId,
            @Valid @RequestBody UpdateUserRequest request) {

        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(
                ApiResponse.success(200, "Cập nhật thông tin cá nhân thành công", response)
        );
    }

    @PutMapping("/{user_id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable("user_id") Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(userId, request);
        return ResponseEntity.ok(
                ApiResponse.success(200, "Đổi mật khẩu thành công", null)
        );
    }

}