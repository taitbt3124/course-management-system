package com.cms.services;

import com.cms.entity.enums.CourseStatus;
import com.cms.models.dtos.requests.CourseRequest;
import com.cms.models.dtos.responses.CourseResponse;

import java.util.List;

public interface CourseService {
    List<CourseResponse> getAllPublishedCourses();

    CourseResponse getCourseDetail(Integer courseId);

    CourseResponse createCourse(CourseRequest request);

    CourseResponse updateCourse(Integer courseId, CourseRequest request);

    CourseResponse updateCourseStatus(Integer courseId, CourseStatus status);

    void deleteCourse(Integer courseId);

    List<CourseResponse> filterCourses(String keyword, Long teacherId, CourseStatus status);
}