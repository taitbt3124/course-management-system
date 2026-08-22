package com.cms.models.dtos.requests;

import com.cms.entity.enums.CourseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCourseStatusRequest {

    @NotNull(message = "Trạng thái khóa học không được để trống")
    private CourseStatus status;
}