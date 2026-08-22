package com.cms.repositories;

import com.cms.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Tìm danh sách khóa học sinh viên đã đăng ký theo userId
    List<Enrollment> findByStudentUserIdOrderByEnrolledAtDesc(Integer studentId);

    boolean existsByStudentUserIdAndCourseCourseId(Integer studentId, Long courseId);

    // Tìm thông tin đăng ký của đúng sinh viên
    Optional<Enrollment> findByEnrollmentIdAndStudentUserId(Long enrollmentId, Integer studentId);

    boolean existsByCourseCourseIdAndStudentUserId(Integer courseId, Integer studentId);

    boolean existsByStudent_UserIdAndCourse_CourseId(Integer studentId, Long courseId);
}