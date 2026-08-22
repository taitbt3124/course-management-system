package com.cms.models.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressReportResponse {
    private Integer studentId;
    private String studentName;
    private String email;
    private Integer totalEnrolledCourses;
    private List<CourseProgressDetailResponse> coursesProgress;
}