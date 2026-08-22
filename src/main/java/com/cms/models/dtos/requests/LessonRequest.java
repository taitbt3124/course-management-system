package com.cms.models.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequest {

    @NotBlank(message = "Tiêu đề bài học không được để trống")
    private String title;

    private String contentUrl;

    private String textContent;

    @NotNull(message = "Thứ tự bài học (orderIndex) không được để trống")
    private Integer orderIndex;

    @Builder.Default
    private Boolean isPublished = false;
}