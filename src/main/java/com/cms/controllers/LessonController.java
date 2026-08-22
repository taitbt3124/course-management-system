package com.cms.controllers;

import com.cms.models.dtos.requests.LessonPublishRequest;
import com.cms.models.dtos.requests.LessonRequest;
import com.cms.models.dtos.responses.ApiResponse;
import com.cms.models.dtos.responses.LessonPreviewResponse;
import com.cms.models.dtos.responses.LessonResponse;
import com.cms.security.principal.UserPrincipal;
import com.cms.services.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/courses/{courseId}/lessons")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getPublishedLessons(
            @PathVariable Long courseId) {

        List<LessonResponse> lessons = lessonService.getPublishedLessonsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(200, "Lấy danh sách bài học thành công", lessons));
    }

    @GetMapping("/lessons/{lessonId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonDetail(@PathVariable Long lessonId) {
        LessonResponse response = lessonService.getPublishedLessonDetail(lessonId);
        return ResponseEntity.ok(ApiResponse.success(200, "Lấy thông tin chi tiết bài học thành công", response));
    }

    @PostMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @PathVariable Long courseId,
            @Valid @RequestBody LessonRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        LessonResponse response = lessonService.createLesson(courseId, request, currentUser);
        return ResponseEntity.status(201).body(ApiResponse.success(201, "Tạo bài học thành công", response));
    }

    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        LessonResponse response = lessonService.updateLesson(lessonId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Cập nhật bài học thành công", response));
    }

    @PutMapping("/lessons/{lessonId}/publish")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLessonStatus(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonPublishRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        LessonResponse response = lessonService.updateLessonStatus(lessonId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Cập nhật trạng thái hiển thị thành công", response));
    }

    @DeleteMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        lessonService.deleteLesson(lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Xóa bài học thành công", null));
    }

    @GetMapping("/lessons/{lessonId}/content_preview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LessonPreviewResponse>> getLessonPreview(@PathVariable Long lessonId) {
        LessonPreviewResponse response = lessonService.getLessonPreview(lessonId);
        return ResponseEntity.ok(ApiResponse.success(200, "Lấy nội dung xem trước bài học thành công", response));
    }
}