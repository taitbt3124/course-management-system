package com.cms.repositories;

import com.cms.entity.Course;
import com.cms.entity.enums.CourseStatus;
import com.cms.models.dtos.responses.TeacherCourseOverviewResponse;
import com.cms.models.dtos.responses.TopCourseReportResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByStatus(CourseStatus status);

    @Query("SELECT c FROM Course c WHERE " +
            "(:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:teacherId IS NULL OR c.teacher.userId = :teacherId) AND " +
            "(:status IS NULL OR c.status = :status)")
    List<Course> searchAndFilterCourses(
            @Param("keyword") String keyword,
            @Param("teacherId") Integer teacherId,
            @Param("status") CourseStatus status
    );

    @Query("SELECT new com.cms.models.dtos.responses.TopCourseReportResponse(" +
            "c.courseId, c.title, c.price, COUNT(e.enrollmentId)) " +
            "FROM Course c " +
            "LEFT JOIN Enrollment e ON c.courseId = e.course.courseId " +
            "GROUP BY c.courseId, c.title, c.price " +
            "ORDER BY COUNT(e.enrollmentId) DESC")
    List<TopCourseReportResponse> findTopCoursesByEnrollments(Pageable pageable);

    @Query(value = "SELECT " +
            "c.course_id AS courseId, " +
            "c.title AS courseTitle, " +
            "CAST(c.status AS text) AS status, " +
            "COUNT(DISTINCT l.lesson_id) AS totalLessons, " +
            "COUNT(DISTINCT e.enrollment_id) AS totalStudents, " +
            "COALESCE(ROUND(AVG(r.rating)::numeric, 1), 0.0) AS averageRating " +
            "FROM courses c " +
            "LEFT JOIN lessons l ON l.course_id = c.course_id " +
            "LEFT JOIN enrollments e ON e.course_id = c.course_id " +
            "LEFT JOIN reviews r ON r.course_id = c.course_id " +
            "WHERE c.teacher_id = :teacherId " +
            "GROUP BY c.course_id, c.title, c.status", nativeQuery = true)
    List<TeacherCourseOverviewResponse> findTeacherCoursesOverview(@Param("teacherId") Integer teacherId);
}