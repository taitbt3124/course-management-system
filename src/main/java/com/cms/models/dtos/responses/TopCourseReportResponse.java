package com.cms.models.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCourseReportResponse {
    private Long courseId;          // Đổi sang Long để khớp với c.courseId trong JPQL
    private String title;           // String
    private BigDecimal price;       // BigDecimal
    private Long totalEnrollments;  // Long (do hàm COUNT trong JPQL trả về Long)
}