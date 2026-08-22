package com.cms.models.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequest {
    @NotNull(message = "User ID không được để trống")
    private Integer userId;

    @NotBlank(message = "Nội dung không được để trống")
    private String message;

    private String type;

    private String targetUrl;
}