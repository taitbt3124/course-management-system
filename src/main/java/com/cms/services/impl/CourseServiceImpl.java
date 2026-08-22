package com.cms.services.impl;

import com.cms.entity.Course;
import com.cms.entity.Lesson;
import com.cms.entity.User;
import com.cms.entity.enums.CourseStatus;
import com.cms.entity.enums.Role;
import com.cms.exceptions.CustomException;
import com.cms.models.constants.ErrorCode;
import com.cms.models.dtos.requests.CourseRequest;
import com.cms.models.dtos.responses.CourseResponse;
import com.cms.models.dtos.responses.LessonResponse;
import com.cms.repositories.CourseRepository;
import com.cms.repositories.LessonRepository;
import com.cms.repositories.UserRepository;
import com.cms.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Override
    public List<CourseResponse> getAllPublishedCourses() {
        List<Course> courses = courseRepository.findByStatus(CourseStatus.PUBLISHED);

        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponse getCourseDetail(Integer courseId) {
        // 1. Tìm khóa học theo ID
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.COURSE_NOT_FOUND, "Không tìm thấy khóa học với ID: " + courseId));

        // 2. Lấy danh sách bài học đã PUBLISHED
        List<Lesson> publishedLessons = lessonRepository
                .findByCourseCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(courseId.longValue());

        // 3. Map danh sách Lesson sang LessonResponse DTO
        List<LessonResponse> lessonResponses = publishedLessons.stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());

        // 4. Map Course sang CourseResponse kèm theo danh sách bài học
        CourseResponse response = mapToCourseResponse(course);
        response.setLessons(lessonResponses);

        return response;
    }

    @Override
    public CourseResponse createCourse(CourseRequest request) {
        // 1. Kiểm tra giảng viên tồn tại và có role TEACHER không
        User teacher = userRepository.findById(request.getTeacherId().intValue())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy giảng viên với ID: " + request.getTeacherId()));

        if (teacher.getRole() != Role.TEACHER) {
            throw new CustomException(ErrorCode.INVALID_INPUT_DATA, "Người dùng được gán không phải là Giảng viên");
        }

        // 2. Tạo đối tượng Course mới với trạng thái mặc định DRAFT
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teacher(teacher)
                .price(request.getPrice())
                .durationHours(request.getDurationHours())
                .status(CourseStatus.DRAFT) // Trạng thái ban đầu luôn là DRAFT
                .build();

        // 3. Lưu vào Database và trả về DTO Response
        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }

    @Override
    public CourseResponse updateCourse(Integer courseId, CourseRequest request) {
        // 1. Kiểm tra tồn tại khóa học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy khóa học với ID: " + courseId));

        // 2. Kiểm tra giảng viên được gán mới có tồn tại và đúng Role TEACHER không
        User teacher = userRepository.findById(request.getTeacherId().intValue())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy giảng viên với ID: " + request.getTeacherId()));

        if (teacher.getRole() != Role.TEACHER) {
            throw new CustomException(ErrorCode.INVALID_INPUT_DATA, "Người dùng được gán không phải là Giảng viên");
        }

        // 3. Cập nhật thông tin
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setTeacher(teacher);
        course.setPrice(request.getPrice());
        course.setDurationHours(request.getDurationHours());

        Course updatedCourse = courseRepository.save(course);
        return mapToCourseResponse(updatedCourse);
    }

    @Override
    public CourseResponse updateCourseStatus(Integer courseId, CourseStatus status) {
        // 1. Kiểm tra tồn tại khóa học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy khóa học với ID: " + courseId));

        // 2. Cập nhật trạng thái
        course.setStatus(status);
        Course updatedCourse = courseRepository.save(course);
        return mapToCourseResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(Integer courseId) {
        // 1. Kiểm tra tồn tại khóa học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy khóa học với ID: " + courseId));

        // 2. Thực hiện xóa khóa học (sẽ cascade xóa các lesson tùy cấu hình FK/JPA)
        courseRepository.delete(course);
    }

    @Override
    public List<CourseResponse> filterCourses(String keyword, Long teacherId, CourseStatus status) {
        // Chuyển Long teacherId sang Integer (nếu Entity User sử dụng Integer làm Primary Key)
        Integer teacherIdInt = teacherId != null ? teacherId.intValue() : null;

        // Truy vấn dữ liệu từ DB
        List<Course> courses = courseRepository.searchAndFilterCourses(
                (keyword != null && !keyword.isBlank()) ? keyword.trim() : null,
                teacherIdInt,
                status
        );

        // Map danh sách Course sang CourseResponse
        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    private CourseResponse mapToCourseResponse(Course course) {
        Long teacherId = null;
        if (course.getTeacher() != null && course.getTeacher().getUserId() != null) {
            teacherId = course.getTeacher().getUserId().longValue();
        }

        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacherId(teacherId)
                .teacherName(course.getTeacher() != null ? course.getTeacher().getFullName() : null)
                .price(course.getPrice())
                .durationHours(course.getDurationHours())
                .status(course.getStatus())
                .build();
    }

    private LessonResponse mapToLessonResponse(Lesson lesson) {
        Long lessonId = lesson.getLessonId() != null ? lesson.getLessonId() : null;
        Long courseId = (lesson.getCourse() != null && lesson.getCourse().getCourseId() != null)
                ? lesson.getCourse().getCourseId().longValue() : null;

        return LessonResponse.builder()
                .lessonId(lessonId)
                .courseId(courseId)
                .title(lesson.getTitle())
                .contentUrl(lesson.getContentUrl())
                .textContent(lesson.getTextContent())
                .orderIndex(lesson.getOrderIndex())
                .isPublished(lesson.getIsPublished())
                .build();
    }
}