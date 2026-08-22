package com.cms.repositories;

import com.cms.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    // Lấy bài học đã PUBLISHED của 1 khóa học, sắp xếp theo thứ tự orderIndex
    List<Lesson> findByCourseCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(Long courseId);

    // Tìm bài học theo lessonId và isPublished = true
    Optional<Lesson> findByLessonIdAndIsPublishedTrue(Long lessonId);
}
