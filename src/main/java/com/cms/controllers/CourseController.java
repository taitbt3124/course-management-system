package com.cms.controllers;

import com.cms.entity.enums.CourseStatus;
import com.cms.models.dtos.requests.CourseRequest;
import com.cms.models.dtos.requests.UpdateCourseStatusRequest;
import com.cms.models.dtos.responses.ApiResponse;
import com.cms.models.dtos.responses.CourseResponse;
import com.cms.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{course_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseDetail(@PathVariable("course_id") Integer courseId) {
        CourseResponse courseDetail = courseService.getCourseDetail(courseId);
        return ResponseEntity.ok(
                ApiResponse.success(200, "Lấy thông tin chi tiết khóa học thành công", courseDetail)
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse createdCourse = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Tạo khóa học thành công", createdCourse));
    }

    @PutMapping("/{course_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable("course_id") Integer courseId,
            @Valid @RequestBody CourseRequest request) {
        CourseResponse updatedCourse = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Cập nhật khóa học thành công", updatedCourse));
    }

    @PutMapping("/{course_id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourseStatus(
            @PathVariable("course_id") Integer courseId,
            @Valid @RequestBody UpdateCourseStatusRequest request) {
        CourseResponse updatedCourse = courseService.updateCourseStatus(courseId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(200, "Cập nhật trạng thái khóa học thành công", updatedCourse));
    }

    @DeleteMapping("/{course_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable("course_id") Integer courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(200, "Xóa khóa học thành công", null));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, name = "teacher_id") Long teacherId,
            @RequestParam(required = false) CourseStatus status) {

        List<CourseResponse> courses = courseService.filterCourses(search, teacherId, status);
        return ResponseEntity.ok(ApiResponse.success(200, "Lấy danh sách khóa học thành công", courses));
    }
}