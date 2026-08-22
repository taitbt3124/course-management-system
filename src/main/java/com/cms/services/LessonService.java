package com.cms.services;

import com.cms.models.dtos.requests.LessonPublishRequest;
import com.cms.models.dtos.requests.LessonRequest;
import com.cms.models.dtos.responses.LessonPreviewResponse;
import com.cms.models.dtos.responses.LessonResponse;
import com.cms.security.principal.UserPrincipal;

import java.util.List;

public interface LessonService {
    List<LessonResponse> getPublishedLessonsByCourse(Long courseId);

    LessonResponse getPublishedLessonDetail(Long lessonId);

    LessonResponse createLesson(Long courseId, LessonRequest request, UserPrincipal currentUser);

    LessonResponse updateLesson(Long lessonId, LessonRequest request, UserPrincipal currentUser);

    LessonResponse updateLessonStatus(Long lessonId, LessonPublishRequest request, UserPrincipal currentUser);

    void deleteLesson(Long lessonId, UserPrincipal currentUser);

    LessonPreviewResponse getLessonPreview(Long lessonId);


}