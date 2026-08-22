package com.cms.models.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    BAD_REQUEST("BAD_REQUEST", HttpStatus.BAD_REQUEST),
    INVALID_INPUT_DATA("INVALID_INPUT_DATA", HttpStatus.BAD_REQUEST),
    BAD_CREDENTIALS("BAD_CREDENTIALS", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT_TOKEN("EXPIRED_JWT_TOKEN", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_TOKEN("INVALID_JWT_TOKEN", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("ACCESS_DENIED", HttpStatus.FORBIDDEN),
    FORBIDDEN_ACTION("FORBIDDEN_ACTION", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND),
    COURSE_NOT_FOUND("COURSE_NOT_FOUND", HttpStatus.NOT_FOUND),
    DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", HttpStatus.BAD_REQUEST),
    INVALID_STATE_TRANSITION("INVALID_STATE_TRANSITION", HttpStatus.CONFLICT),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    // Khai báo đúng cú pháp có tham số truyền vào:
    COURSE_NOT_ENROLLED("COURSE_NOT_ENROLLED", HttpStatus.FORBIDDEN),
    REVIEW_ALREADY_EXISTS("REVIEW_ALREADY_EXISTS", HttpStatus.BAD_REQUEST),
    ALREADY_EXISTS("ALREADY_EXISTS", HttpStatus.BAD_REQUEST);

    private final String code;
    private final HttpStatus httpStatus;

    ErrorCode(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }
}