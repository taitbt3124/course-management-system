package com.cms.services;

import com.cms.models.dtos.requests.NotificationRequest;
import com.cms.models.dtos.responses.NotificationResponse;
import com.cms.security.principal.UserPrincipal;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getUserNotifications(UserPrincipal currentUser);
    NotificationResponse markAsRead(Long notificationId, UserPrincipal currentUser);
    NotificationResponse createNotification(NotificationRequest request);
    void deleteNotification(Long notificationId);
}