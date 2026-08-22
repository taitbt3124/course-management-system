package com.cms.services.impl;

import com.cms.entity.Course;
import com.cms.entity.Review;
import com.cms.entity.User;
import com.cms.entity.enums.Role;
import com.cms.exceptions.CustomException;
import com.cms.exceptions.NotFoundException;
import com.cms.models.constants.ErrorCode;
import com.cms.models.dtos.requests.ReviewRequest;
import com.cms.models.dtos.responses.ReviewResponse;
import com.cms.repositories.CourseRepository;
import com.cms.repositories.EnrollmentRepository;
import com.cms.repositories.ReviewRepository;
import com.cms.repositories.UserRepository;
import com.cms.security.principal.UserPrincipal;
import com.cms.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByCourse(Long courseId) {
        if (!courseRepository.existsById(Math.toIntExact(courseId))) {
            throw new NotFoundException("Không tìm thấy khóa học với ID: " + courseId);
        }
        return reviewRepository.findByCourse_CourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReviewResponse createReview(Long courseId, ReviewRequest request, UserPrincipal currentUser) {
        // Sửa lỗi Ảnh 2: Dùng getUserId() thay vì getId()
        Integer userId = currentUser.getUserId();

        // 1. Kiểm tra khóa học có tồn tại
        Course course = courseRepository.findById(Math.toIntExact(courseId))
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        // 2. Kiểm tra học sinh đã đăng ký khóa học chưa (Sửa lỗi Ảnh 3)
        boolean isEnrolled = enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(userId, courseId);
        if (!isEnrolled) {
            // Sửa lỗi Ảnh 4: Dùng ErrorCode theo đúng constructor CustomException
            throw new CustomException(ErrorCode.COURSE_NOT_ENROLLED, "Bạn chưa đăng ký khóa học này nên không thể đánh giá");
        }

        // 3. Kiểm tra đã gửi đánh giá trước đó chưa (Sửa lỗi Ảnh 1: courseId kiểu Long)
        if (reviewRepository.existsByCourse_CourseIdAndUser_UserId(courseId, userId)) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS, "Bạn đã đánh giá khóa học này rồi");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + userId));

        Review review = Review.builder()
                .course(course)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Integer reviewId, ReviewRequest request, UserPrincipal currentUser) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài đánh giá với ID: " + reviewId));

        checkOwnerOrAdminPermission(review, currentUser);

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);
        return mapToResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Integer reviewId, UserPrincipal currentUser) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bài đánh giá với ID: " + reviewId));

        checkOwnerOrAdminPermission(review, currentUser);

        reviewRepository.delete(review);
    }

    private void checkOwnerOrAdminPermission(Review review, UserPrincipal currentUser) {
        boolean isOwner = review.getUser().getUserId().equals(currentUser.getUserId());
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Role.ADMIN.name()) || a.getAuthority().equals(Role.ADMIN.name()));

        if (!isOwner && !isAdmin) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền thực hiện thao tác trên đánh giá này");
        }
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .courseId(review.getCourse().getCourseId())
                .userId(review.getUser().getUserId())
                .username(review.getUser().getUsername())
                .fullName(review.getUser().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}