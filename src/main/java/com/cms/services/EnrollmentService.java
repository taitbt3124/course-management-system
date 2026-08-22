package com.cms.services;

import com.cms.models.dtos.requests.EnrollmentRequest;
import com.cms.models.dtos.responses.EnrollmentDetailResponse;
import com.cms.models.dtos.responses.EnrollmentResponse;
import com.cms.security.principal.UserPrincipal;

import java.util.List;

public interface EnrollmentService {
    List<EnrollmentResponse> getMyEnrollments(UserPrincipal currentUser);

    // 1. Đăng ký khóa học
    EnrollmentResponse enrollCourse(EnrollmentRequest request, UserPrincipal currentUser);

    // 2. Chi tiết đăng ký & tiến độ
    EnrollmentDetailResponse getEnrollmentDetail(Long enrollmentId, UserPrincipal currentUser);

    // 3. Đánh dấu hoàn thành bài học
    EnrollmentDetailResponse completeLesson(Long enrollmentId, Long lessonId, UserPrincipal currentUser);
}