package com.cms.repositories;

import com.cms.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByCourse_CourseId(Long courseId);

    boolean existsByCourse_CourseIdAndUser_UserId(Long courseId, Integer userId);
}