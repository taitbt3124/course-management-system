package com.cms.services.impl;

import com.cms.entity.User;
import com.cms.entity.enums.Role;
import com.cms.exceptions.CustomException;
import com.cms.models.constants.ErrorCode;
import com.cms.models.dtos.requests.*;
import com.cms.models.dtos.responses.UserResponse;
import com.cms.repositories.UserRepository;
import com.cms.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.cms.entity.enums.Role;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        // 1. Lấy user đang thực hiện request từ SecurityContext
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Tài khoản hiện tại không tồn tại"));

        // 2. Chuyển ID về kiểu primitive long để so sánh chính xác giá trị
        long currentUserId = currentUser.getUserId();
        long targetUserId = userId;

        // 3. Check phân quyền: Nếu KHÔNG PHẢI ADMIN và KHÔNG PHẢI CHÍNH MÌNH -> Chặn 403
        if (currentUser.getRole() != Role.ADMIN && currentUserId != targetUserId) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền cập nhật thông tin người dùng này");
        }

        // 4. Lấy thông tin user cần cập nhật
        User userToUpdate = userRepository.findById(userId.intValue())
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Không tìm thấy người dùng với ID: " + userId));

        // 5. Kiểm tra email trùng lặp (nếu thay đổi email)
        if (!userToUpdate.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.BAD_CREDENTIALS, "Email đã được sử dụng bởi tài khoản khác");
        }

        // 6. Cập nhật và lưu vào DB
        userToUpdate.setFullName(request.getFullName());
        userToUpdate.setEmail(request.getEmail());

        User updatedUser = userRepository.save(userToUpdate);
        return mapToUserResponse(updatedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }


    @Override
    public UserResponse getUserById(Long userId) {
        // 1. Lấy thông tin người dùng đang gọi API từ SecurityContext
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Người dùng hiện tại không tồn tại"));

        // 2. Kiểm tra phân quyền: Nếu không phải ADMIN và cố xem profile người khác -> Chặn
        if (currentUser.getRole() != Role.ADMIN && !currentUser.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền xem thông tin người dùng này");
        }

        // 3. Tìm thông tin user được yêu cầu
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Không tìm thấy người dùng với ID: " + userId));

        return mapToUserResponse(user);
    }

    private final PasswordEncoder passwordEncoder; // Thêm injection này

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        // 1. Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.BAD_CREDENTIALS, "Tên đăng nhập đã tồn tại");
        }

        // 2. Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.BAD_CREDENTIALS, "Email đã được sử dụng");
        }

        // 3. Khởi tạo và lưu User mới
        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole())
                .isActive(true) // Mặc định tài khoản mới tạo sẽ kích hoạt luôn
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse updateUserRole(Long userId, UpdateRoleRequest request) {
        // 1. Lấy thông tin ADMIN đang thực hiện thao tác
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentAdmin = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Tài khoản hiện tại không tồn tại"));

        // 2. Tìm người dùng cần thay đổi role
        User targetUser = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Không tìm thấy người dùng với ID: " + userId));

        // 3. Check ràng buộc: Target User đã là ADMIN -> Không cho phép sửa (dù là ADMIN khác hay chính mình)
        if (targetUser.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "Không được phép thay đổi vai trò của tài khoản ADMIN khác");
        }

        // 4. Cập nhật Role mới
        targetUser.setRole(request.getRole());
        User updatedUser = userRepository.save(targetUser);

        return mapToUserResponse(updatedUser);
    }

    @Override
    public UserResponse updateUserStatus(Long userId, UpdateStatusRequest request) {
        // 1. Lấy thông tin ADMIN đang thao tác
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentAdmin = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Tài khoản hiện tại không tồn tại"));

        // 2. Tìm người dùng cần thay đổi trạng thái
        User targetUser = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Không tìm thấy người dùng với ID: " + userId));

        // 3. Ràng buộc bảo mật: Không cho phép đổi trạng thái của tài khoản ADMIN (bao gồm chính mình)
        if (targetUser.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "Không được phép thay đổi trạng thái của tài khoản ADMIN");
        }

        // 4. Cập nhật trạng thái mới
        targetUser.setIsActive(request.getStatus());
        User updatedUser = userRepository.save(targetUser);

        return mapToUserResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        // 1. Tìm user cần xóa trong DB
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Không tìm thấy người dùng với ID: " + userId));

        // 2. Ràng buộc bảo mật: Không cho phép xóa tài khoản ADMIN
        if (user.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "Không được phép xóa tài khoản ADMIN");
        }

        // 3. Thực hiện Soft Delete
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        // 1. Lấy thông tin user đang thực hiện thao tác
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Tài khoản hiện tại không tồn tại"));

        // 2. Phân quyền: Không phải ADMIN và cũng không phải chính mình -> Chặn 403
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isSelf = currentUser.getUserId().equals(userId.intValue());

        if (!isAdmin && !isSelf) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền đổi mật khẩu cho tài khoản này");
        }

        // 3. Tìm user cần đổi mật khẩu
        User targetUser = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Không tìm thấy người dùng với ID: " + userId));

        // 4. Nếu là chính chủ tự đổi mật khẩu (không phải ADMIN can thiệp) -> Bắt buộc kiểm tra mật khẩu cũ
        if (isSelf && !isAdmin) {
            if (request.getOldPassword() == null || request.getOldPassword().isBlank()) {
                throw new CustomException(ErrorCode.BAD_CREDENTIALS, "Vui lòng nhập mật khẩu cũ");
            }
            if (!passwordEncoder.matches(request.getOldPassword(), targetUser.getPasswordHash())) {
                throw new CustomException(ErrorCode.BAD_CREDENTIALS, "Mật khẩu cũ không chính xác");
            }
        }

        // 5. Cập nhật mật khẩu mới (Mã hóa trước khi lưu)
        targetUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(targetUser);
    }

    @Override
    public List<UserResponse> getUsersByStatus(Boolean status) {
        // 1. Kiểm tra phân quyền ADMIN
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS, "Tài khoản hiện tại không tồn tại"));

        if (currentUser.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "Chỉ ADMIN mới có quyền xem danh sách người dùng");
        }

        // 2. Nếu truyền tham số status -> Lọc theo status. Nếu không truyền (null) -> Lấy tất cả
        List<User> users;
        if (status != null) {
            users = userRepository.findByIsActive(status);
        } else {
            users = userRepository.findAll();
        }

        // 3. Map danh sách Entity sang Response DTO
        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getAllUsers(Role role, Boolean status) {
        List<User> users = userRepository.findAll();

        // Lọc theo Role nếu có truyền param
        if (role != null) {
            users = users.stream()
                    .filter(u -> u.getRole() == role)
                    .collect(Collectors.toList());
        }

        // Lọc theo Status (active) nếu có truyền param
        if (status != null) {
            users = users.stream()
                    .filter(u -> Boolean.valueOf(u.getIsActive()).equals(status))
                    .collect(Collectors.toList());
        }

        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

}