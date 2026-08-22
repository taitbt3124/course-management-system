package com.cms.services.impl;

import com.cms.entity.Course;
import com.cms.entity.Enrollment;
import com.cms.entity.Lesson;
import com.cms.entity.User;
import com.cms.entity.enums.EnrollmentStatus;
import com.cms.exceptions.CustomException;
import com.cms.exceptions.NotFoundException;
import com.cms.models.constants.ErrorCode;
import com.cms.models.dtos.requests.EnrollmentRequest;
import com.cms.models.dtos.responses.EnrollmentDetailResponse;
import com.cms.models.dtos.responses.EnrollmentResponse;
import com.cms.repositories.CourseRepository;
import com.cms.repositories.EnrollmentRepository;
import com.cms.repositories.LessonRepository;
import com.cms.repositories.UserRepository;
import com.cms.security.principal.UserPrincipal;
import com.cms.services.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    @Override
    public List<EnrollmentResponse> getMyEnrollments(UserPrincipal currentUser) {
        Integer studentId = currentUser.getUserId();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentUserIdOrderByEnrolledAtDesc(studentId);

        return enrollments.stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    // 1. POST /api/enrollments - Đăng ký khóa học mới
    @Override
    @Transactional
    public EnrollmentResponse enrollCourse(EnrollmentRequest request, UserPrincipal currentUser) {
        Integer studentId = currentUser.getUserId();

        // Kiểm tra xem đã đăng ký chưa
        if (enrollmentRepository.existsByStudentUserIdAndCourseCourseId(studentId, request.getCourseId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "Bạn đã đăng ký khóa học này rồi");
        }

        Course course = courseRepository.findById(Math.toIntExact(request.getCourseId()))
                .orElseThrow(() -> new NotFoundException("Khóa học không tồn tại với ID: " + request.getCourseId()));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Sinh viên không tồn tại"));

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .status(EnrollmentStatus.ENROLLED)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return mapToEnrollmentResponse(savedEnrollment);
    }

    // 2. GET /api/enrollments/{enrollment_id} - Lấy chi tiết thông tin đăng ký & tiến độ
    @Override
    public EnrollmentDetailResponse getEnrollmentDetail(Long enrollmentId, UserPrincipal currentUser) {
        Enrollment enrollment = getStudentEnrollment(enrollmentId, currentUser.getUserId());
        return mapToEnrollmentDetailResponse(enrollment);
    }

    // 3. PUT /api/enrollments/{enrollment_id}/complete_lesson/{lesson_id} - Hoàn thành bài học
    @Override
    @Transactional
    public EnrollmentDetailResponse completeLesson(Long enrollmentId, Long lessonId, UserPrincipal currentUser) {
        Enrollment enrollment = getStudentEnrollment(enrollmentId, currentUser.getUserId());

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Bài học không tồn tại với ID: " + lessonId));

        // Kiểm tra bài học có thuộc khóa học này không
        if (!lesson.getCourse().getCourseId().equals(enrollment.getCourse().getCourseId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "Bài học không thuộc khóa học đã đăng ký");
        }

        // Logic thêm bài học vào danh sách bài học đã hoàn thành
        if (enrollment.getCompletedLessons() == null) {
            enrollment.setCompletedLessons(new ArrayList<>());
        }

        if (!enrollment.getCompletedLessons().contains(lesson)) {
            enrollment.getCompletedLessons().add(lesson);
            enrollmentRepository.save(enrollment);
        }

        return mapToEnrollmentDetailResponse(enrollment);
    }

    // Helper: Tìm enrollment và kiểm tra quyền sở hữu của Student
    private Enrollment getStudentEnrollment(Long enrollmentId, Integer studentId) {
        return enrollmentRepository.findByEnrollmentIdAndStudentUserId(enrollmentId, studentId)
                .orElseThrow(() -> new NotFoundException("Thông tin đăng ký không tồn tại hoặc bạn không có quyền truy cập"));
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getEnrollmentId())
                .courseId(enrollment.getCourse().getCourseId())
                .courseTitle(enrollment.getCourse().getTitle())
                .courseDescription(enrollment.getCourse().getDescription())
                .teacherName(enrollment.getCourse().getTeacher() != null
                        ? enrollment.getCourse().getTeacher().getFullName()
                        : null)
                .enrolledAt(enrollment.getEnrolledAt())
                .status(enrollment.getStatus() != null ? enrollment.getStatus().name() : null)
                .build();
    }

    private EnrollmentDetailResponse mapToEnrollmentDetailResponse(Enrollment enrollment) {
        List<Lesson> allPublishedLessons = lessonRepository
                .findByCourseCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(enrollment.getCourse().getCourseId());

        int totalLessons = allPublishedLessons.size();

        List<Long> completedIds = enrollment.getCompletedLessons() != null
                ? enrollment.getCompletedLessons().stream().map(Lesson::getLessonId).collect(Collectors.toList())
                : new ArrayList<>();

        int completedCount = completedIds.size();
        double progress = totalLessons > 0 ? ((double) completedCount / totalLessons) * 100 : 0.0;

        return EnrollmentDetailResponse.builder()
                .enrollmentId(enrollment.getEnrollmentId())
                .courseId(enrollment.getCourse().getCourseId())
                .courseTitle(enrollment.getCourse().getTitle())
                .courseDescription(enrollment.getCourse().getDescription())
                .teacherName(enrollment.getCourse().getTeacher() != null
                        ? enrollment.getCourse().getTeacher().getFullName()
                        : null)
                .enrolledAt(enrollment.getEnrolledAt())
                .status(enrollment.getStatus() != null ? enrollment.getStatus().name() : null)
                .totalLessons(totalLessons)
                .completedLessons(completedCount)
                .progressPercentage(Math.round(progress * 100.0) / 100.0)
                .completedLessonIds(completedIds)
                .build();
    }
}