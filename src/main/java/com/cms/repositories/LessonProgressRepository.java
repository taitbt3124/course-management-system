package com.cms.repositories;

import com.cms.entity.LessonProgress;
import com.cms.models.dtos.responses.CourseProgressDetailResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Integer> {

    @Query("SELECT new com.cms.models.dtos.responses.CourseProgressDetailResponse(" +
            "c.courseId, " +
            "c.title, " +
            "COUNT(DISTINCT l.lessonId), " +
            "SUM(CASE WHEN lp.isCompleted = true THEN 1L ELSE 0L END), " +
            "CAST(CASE WHEN COUNT(DISTINCT l.lessonId) > 0 " +
            "     THEN (1.0 * SUM(CASE WHEN lp.isCompleted = true THEN 1 ELSE 0 END) / COUNT(DISTINCT l.lessonId)) * 100.0 " +
            "     ELSE 0.0 END AS double)) " +
            "FROM Enrollment e " +
            "JOIN e.course c " +
            "LEFT JOIN Lesson l ON l.course.courseId = c.courseId AND l.isPublished = true " +
            "LEFT JOIN LessonProgress lp ON lp.lesson.lessonId = l.lessonId AND lp.enrollment.enrollmentId = e.enrollmentId " +
            "WHERE e.student.userId = :studentId " +
            "GROUP BY c.courseId, c.title")
    List<CourseProgressDetailResponse> findProgressByStudentId(@Param("studentId") Integer studentId);
}