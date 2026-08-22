package com.cms.exceptions;

import com.cms.models.constants.ErrorCode;

public class AuthException extends CustomException {
    public AuthException(String message) {
        super(ErrorCode.BAD_CREDENTIALS, message);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}