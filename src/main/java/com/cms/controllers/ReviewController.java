package com.cms.controllers;

import com.cms.models.dtos.requests.ReviewRequest;
import com.cms.models.dtos.responses.ApiResponse;
import com.cms.models.dtos.responses.ReviewResponse;
import com.cms.security.principal.UserPrincipal;
import com.cms.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/courses/{courseId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByCourse(@PathVariable Long courseId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.<List<ReviewResponse>>builder()
                .success(true)
                .message("Lấy danh sách đánh giá thành công")
                .data(reviews)
                .build());
    }

    @PostMapping("/api/courses/{courseId}/reviews")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long courseId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ReviewResponse response = reviewService.createReview(courseId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Tạo đánh giá thành công")
                .data(response)
                .build());
    }

    @PutMapping("/api/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Integer reviewId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ReviewResponse response = reviewService.updateReview(reviewId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Cập nhật đánh giá thành công")
                .data(response)
                .build());
    }

    @DeleteMapping("/api/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Integer reviewId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        reviewService.deleteReview(reviewId, currentUser);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa đánh giá thành công")
                .build());
    }
}