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
public class TeacherOverviewReportResponse {
    private Integer teacherId;
    private String teacherName;
    private String email;
    private Integer totalCourses;
    private List<TeacherCourseOverviewResponse> courses;
}