package com.cms.models.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDetailResponse {
    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String teacherName;
    private LocalDateTime enrolledAt;
    private String status;
    private Integer totalLessons;
    private Integer completedLessons;
    private Double progressPercentage; // Tiến độ học tập (%)
    private List<Long> completedLessonIds; // Danh sách ID các bài học đã hoàn thành
}