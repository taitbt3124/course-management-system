package com.cms.models.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonProperty("error_code")
    private String errorCode;

    private String message;

    private T data;

    private Object errors;

    @Builder.Default
    private String timestamp = Instant.now().toString();

    // Helper method cho Success
    public static <T> ApiResponse<T> success(Integer statusCode, String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .build();
    }

    // Helper method cho Error
    public static <T> ApiResponse<T> error(Integer statusCode, String errorCode, String message, Object errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(statusCode)
                .errorCode(errorCode)
                .message(message)
                .errors(errors)
                .build();
    }
}