package com.cms.controllers;

import com.cms.models.dtos.requests.EnrollmentRequest;
import com.cms.models.dtos.responses.ApiResponse;
import com.cms.models.dtos.responses.EnrollmentDetailResponse;
import com.cms.models.dtos.responses.EnrollmentResponse;
import com.cms.security.principal.UserPrincipal;
import com.cms.services.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<EnrollmentResponse> response = enrollmentService.getMyEnrollments(currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Lấy danh sách khóa học đã đăng ký thành công", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(
            @Valid @RequestBody EnrollmentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        EnrollmentResponse response = enrollmentService.enrollCourse(request, currentUser);
        return ResponseEntity.status(201).body(ApiResponse.success(201, "Đăng ký khóa học thành công", response));
    }

    @GetMapping("/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentDetailResponse>> getEnrollmentDetail(
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        EnrollmentDetailResponse response = enrollmentService.getEnrollmentDetail(enrollmentId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Lấy chi tiết tiến độ thành công", response));
    }

    @PutMapping("/{enrollmentId}/complete_lesson/{lessonId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentDetailResponse>> completeLesson(
            @PathVariable Long enrollmentId,
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        EnrollmentDetailResponse response = enrollmentService.completeLesson(enrollmentId, lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Đánh dấu bài học hoàn thành thành công", response));
    }
}