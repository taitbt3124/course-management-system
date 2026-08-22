package com.cms.services;

import com.cms.models.dtos.requests.ReviewRequest;
import com.cms.models.dtos.responses.ReviewResponse;
import com.cms.security.principal.UserPrincipal;

import java.util.List;

public interface ReviewService {

    List<ReviewResponse> getReviewsByCourse(Long courseId);

    ReviewResponse createReview(Long courseId, ReviewRequest request, UserPrincipal currentUser);

    ReviewResponse updateReview(Integer reviewId, ReviewRequest request, UserPrincipal currentUser);

    void deleteReview(Integer reviewId, UserPrincipal currentUser);
}