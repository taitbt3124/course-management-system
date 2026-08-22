package com.cms.models.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Tên khóa học không được để trống")
    private String title;

    private String description;

    @NotNull(message = "ID giảng viên không được để trống")
    private Long teacherId;

    @NotNull(message = "Giá khóa học không được để trống")
    private BigDecimal price;

    @NotNull(message = "Thời lượng khóa học không được để trống")
    @Positive(message = "Thời lượng phải lớn hơn 0")
    private Integer durationHours;
}