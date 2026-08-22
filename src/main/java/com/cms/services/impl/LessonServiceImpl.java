package com.cms.services.impl;

import com.cms.entity.Course;
import com.cms.entity.Lesson;
import com.cms.entity.enums.Role;
import com.cms.exceptions.CustomException;
import com.cms.exceptions.NotFoundException;
import com.cms.models.constants.ErrorCode;
import com.cms.models.dtos.requests.LessonPublishRequest;
import com.cms.models.dtos.requests.LessonRequest;
import com.cms.models.dtos.responses.LessonPreviewResponse;
import com.cms.models.dtos.responses.LessonResponse;
import com.cms.repositories.CourseRepository;
import com.cms.repositories.LessonRepository;
import com.cms.security.principal.UserPrincipal;
import com.cms.services.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Override
    public List<LessonResponse> getPublishedLessonsByCourse(Long courseId) {
        if (!courseRepository.existsById(Math.toIntExact(courseId))) {
            throw new NotFoundException("Khóa học không tồn tại với ID: " + courseId);
        }

        List<Lesson> lessons = lessonRepository.findByCourseCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(courseId);

        return lessons.stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LessonResponse getPublishedLessonDetail(Long lessonId) {
        Lesson lesson = lessonRepository.findByLessonIdAndIsPublishedTrue(lessonId)
                .orElseThrow(() -> new NotFoundException("Bài học không tồn tại hoặc chưa được xuất bản"));

        return mapToLessonResponse(lesson);
    }

    @Override
    @Transactional
    public LessonResponse createLesson(Long courseId, LessonRequest request, UserPrincipal currentUser) {
        Course course = courseRepository.findById(Math.toIntExact(courseId))
                .orElseThrow(() -> new NotFoundException("Khóa học không tồn tại với ID: " + courseId));

        validateOwnership(course, currentUser);

        Lesson lesson = Lesson.builder()
                .course(course)
                .title(request.getTitle())
                .contentUrl(request.getContentUrl())
                .textContent(request.getTextContent())
                .orderIndex(request.getOrderIndex())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);
        return mapToLessonResponse(savedLesson);
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(Long lessonId, LessonRequest request, UserPrincipal currentUser) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Bài học không tồn tại với ID: " + lessonId));

        validateOwnership(lesson.getCourse(), currentUser);

        lesson.setTitle(request.getTitle());
        lesson.setContentUrl(request.getContentUrl());
        lesson.setTextContent(request.getTextContent());
        lesson.setOrderIndex(request.getOrderIndex());
        if (request.getIsPublished() != null) {
            lesson.setIsPublished(request.getIsPublished());
        }

        Lesson updatedLesson = lessonRepository.save(lesson);
        return mapToLessonResponse(updatedLesson);
    }

    @Override
    @Transactional
    public LessonResponse updateLessonStatus(Long lessonId, LessonPublishRequest request, UserPrincipal currentUser) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Bài học không tồn tại với ID: " + lessonId));

        validateOwnership(lesson.getCourse(), currentUser);

        lesson.setIsPublished(request.getIsPublished());

        Lesson updatedLesson = lessonRepository.save(lesson);
        return mapToLessonResponse(updatedLesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId, UserPrincipal currentUser) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Bài học không tồn tại với ID: " + lessonId));

        validateOwnership(lesson.getCourse(), currentUser);

        lessonRepository.delete(lesson);
    }

    private void validateOwnership(Course course, UserPrincipal currentUser) {
        if (currentUser.getRole() == Role.TEACHER) {
            Integer teacherId = course.getTeacher().getUserId();
            Integer currentUserId = currentUser.getUserId();

            if (teacherId == null || !teacherId.equals(currentUserId)) {
                throw new CustomException(ErrorCode.FORBIDDEN_ACTION, "Bạn chỉ được phép thao tác trên khóa học do mình phụ trách");
            }
        }
    }

    private LessonResponse mapToLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .lessonId(lesson.getLessonId())
                .courseId(lesson.getCourse().getCourseId())
                .title(lesson.getTitle())
                .contentUrl(lesson.getContentUrl())
                .textContent(lesson.getTextContent())
                .orderIndex(lesson.getOrderIndex())
                .isPublished(lesson.getIsPublished())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }

    @Override
    public LessonPreviewResponse getLessonPreview(Long lessonId) {
        // Chỉ lấy những bài học đã được publish cho học viên xem trước
        Lesson lesson = lessonRepository.findByLessonIdAndIsPublishedTrue(lessonId)
                .orElseThrow(() -> new NotFoundException("Bài học không tồn tại hoặc chưa được xuất bản"));

        return LessonPreviewResponse.builder()
                .lessonId(lesson.getLessonId())
                .courseId(lesson.getCourse().getCourseId())
                .title(lesson.getTitle())
                .contentUrl(lesson.getContentUrl())
                .textContent(lesson.getTextContent())
                .orderIndex(lesson.getOrderIndex())
                .isPublished(lesson.getIsPublished())
                .build();
    }
}