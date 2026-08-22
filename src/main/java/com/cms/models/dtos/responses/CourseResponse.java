package com.cms.models.dtos.responses;

import com.cms.entity.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long courseId; // Đổi từ Long sang Integer
    private String title;
    private String description;
    private Long teacherId;
    private String teacherName;
    private BigDecimal price;
    private Integer durationHours;
    private CourseStatus status;
    private List<LessonResponse> lessons;
}