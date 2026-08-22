package com.cms.models.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonPreviewResponse {
    private Long lessonId;
    private Long courseId;
    private String title;
    private String contentUrl;
    private String textContent;
    private Integer orderIndex;
    private Boolean isPublished;
}