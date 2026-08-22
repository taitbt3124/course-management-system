package com.cms.models.dtos.responses;

public interface TeacherCourseOverviewResponse {
    Integer getCourseId();
    String getCourseTitle();
    String getStatus();
    Long getTotalLessons();
    Long getTotalStudents();
    Double getAverageRating();
}