package com.cms.services;

import com.cms.models.dtos.responses.StudentProgressReportResponse;
import com.cms.models.dtos.responses.TeacherOverviewReportResponse;
import com.cms.models.dtos.responses.TopCourseReportResponse;

import java.util.List;

public interface ReportService {
    StudentProgressReportResponse getStudentProgressReport(Integer studentId);
    List<TopCourseReportResponse> getTopCoursesByEnrollments(int limit);
    TeacherOverviewReportResponse getTeacherCoursesOverview(Integer teacherId);
}