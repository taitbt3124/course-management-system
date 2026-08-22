package com.cms.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Không tìm thấy tài nguyên yêu cầu");
    }

    public NotFoundException(String message) {
        super(message);
    }
}