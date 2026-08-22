package com.cms.models.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressDetailResponse {
    private Integer courseId;
    private String courseTitle;
    private Long totalLessons;
    private Long completedLessons;
    private Double progressPercentage;

    // Constructor phục vụ riêng cho JPQL Instantiation
    public CourseProgressDetailResponse(Object courseId, Object courseTitle, Object totalLessons, Object completedLessons, Object progressPercentage) {
        this.courseId = courseId != null ? ((Number) courseId).intValue() : null;
        this.courseTitle = courseTitle != null ? courseTitle.toString() : "";
        this.totalLessons = totalLessons != null ? ((Number) totalLessons).longValue() : 0L;
        this.completedLessons = completedLessons != null ? ((Number) completedLessons).longValue() : 0L;
        this.progressPercentage = progressPercentage != null ? ((Number) progressPercentage).doubleValue() : 0.0;
    }
}