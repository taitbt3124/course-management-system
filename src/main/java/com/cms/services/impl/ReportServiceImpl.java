package com.cms.services.impl;

import com.cms.entity.User;
import com.cms.entity.enums.Role;
import com.cms.exceptions.NotFoundException;
import com.cms.models.dtos.responses.CourseProgressDetailResponse;
import com.cms.models.dtos.responses.StudentProgressReportResponse;
import com.cms.models.dtos.responses.TeacherCourseOverviewResponse; // <-- Bổ sung import này
import com.cms.models.dtos.responses.TeacherOverviewReportResponse; // <-- Bổ sung import này
import com.cms.models.dtos.responses.TopCourseReportResponse;
import com.cms.repositories.CourseRepository;
import com.cms.repositories.LessonProgressRepository;
import com.cms.repositories.UserRepository;
import com.cms.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonProgressRepository lessonProgressRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentProgressReportResponse getStudentProgressReport(Integer studentId) {
        User student = userRepository.findById(studentId)
                .filter(user -> user.getRole() == Role.STUDENT)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sinh viên với ID: " + studentId));

        List<CourseProgressDetailResponse> courseProgressList =
                lessonProgressRepository.findProgressByStudentId(studentId);

        return StudentProgressReportResponse.builder()
                .studentId(student.getUserId())
                .studentName(student.getFullName())
                .email(student.getEmail())
                .totalEnrolledCourses(courseProgressList.size())
                .coursesProgress(courseProgressList)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopCourseReportResponse> getTopCoursesByEnrollments(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return courseRepository.findTopCoursesByEnrollments(pageable);
    }

    // ================= BỔ SUNG METHOD NÀY =================
    @Override
    @Transactional(readOnly = true)
    public TeacherOverviewReportResponse getTeacherCoursesOverview(Integer teacherId) {
        User teacher = userRepository.findById(teacherId)
                .filter(user -> user.getRole() == Role.TEACHER)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giảng viên với ID: " + teacherId));

        List<TeacherCourseOverviewResponse> courses = courseRepository.findTeacherCoursesOverview(teacherId);

        return TeacherOverviewReportResponse.builder()
                .teacherId(teacher.getUserId())
                .teacherName(teacher.getFullName())
                .email(teacher.getEmail())
                .totalCourses(courses.size())
                .courses(courses)
                .build();
    }
}