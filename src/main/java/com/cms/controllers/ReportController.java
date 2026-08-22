package com.cms.controllers;

import com.cms.models.dtos.responses.StudentProgressReportResponse;
import com.cms.models.dtos.responses.TeacherOverviewReportResponse;
import com.cms.models.dtos.responses.TopCourseReportResponse;
import com.cms.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/students/{studentId}/progress")
    public ResponseEntity<StudentProgressReportResponse> getStudentProgressReport(
            @PathVariable Integer studentId) {
        return ResponseEntity.ok(reportService.getStudentProgressReport(studentId));
    }

    @GetMapping("/top-courses")
    public ResponseEntity<List<TopCourseReportResponse>> getTopCourses(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reportService.getTopCoursesByEnrollments(limit));
    }

    @GetMapping("/teacher_courses_overview/{teacher_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherOverviewReportResponse> getTeacherCoursesOverview(
            @PathVariable("teacher_id") Integer teacherId) {
        return ResponseEntity.ok(reportService.getTeacherCoursesOverview(teacherId));
    }
}