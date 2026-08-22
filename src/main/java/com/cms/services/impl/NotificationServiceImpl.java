package com.cms.services.impl;

import com.cms.entity.Notification;
import com.cms.entity.User;
import com.cms.exceptions.CustomException;
import com.cms.exceptions.NotFoundException;
import com.cms.models.constants.ErrorCode;
import com.cms.models.dtos.requests.NotificationRequest;
import com.cms.models.dtos.responses.NotificationResponse;
import com.cms.repositories.NotificationRepository;
import com.cms.repositories.UserRepository;
import com.cms.security.principal.UserPrincipal;
import com.cms.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(UserPrincipal currentUser) {
        List<Notification> notifications = notificationRepository
                .findByRecipientUserIdOrderByCreatedAtDesc(currentUser.getUserId());
        return notifications.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, UserPrincipal currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Thông báo không tồn tại với ID: " + notificationId));

        if (!notification.getRecipient().getUserId().equals(currentUser.getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "Bạn không có quyền cập nhật thông báo này");
        }

        notification.setIsRead(true);
        return mapToResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        User recipient = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại với ID: " + request.getUserId()));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(request.getMessage())
                .type(request.getType())
                .targetUrl(request.getTargetUrl())
                .build();

        return mapToResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new NotFoundException("Thông báo không tồn tại với ID: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getRecipient().getUserId())
                .message(notification.getMessage())
                .type(notification.getType())
                .targetUrl(notification.getTargetUrl())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}