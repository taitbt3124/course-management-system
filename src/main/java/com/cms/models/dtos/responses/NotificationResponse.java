package com.cms.models.dtos.responses;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long notificationId;
    private Integer userId;
    private String message;
    private String type;
    private String targetUrl;
    private Boolean isRead;
    private LocalDateTime createdAt;
}