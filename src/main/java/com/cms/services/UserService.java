package com.cms.services;

import com.cms.models.dtos.requests.*;
import com.cms.models.dtos.responses.UserResponse;
import com.cms.entity.enums.Role;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers(Role role, Boolean status);
    UserResponse getUserById(Long userId);
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    void deleteUser(Long userId);
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUserRole(Long userId, UpdateRoleRequest request);
    UserResponse updateUserStatus(Long userId, UpdateStatusRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
    List<UserResponse> getUsersByStatus(Boolean status);

}