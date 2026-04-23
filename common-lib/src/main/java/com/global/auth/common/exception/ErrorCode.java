package com.global.auth.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    COMMON_ERROR("C001", "Internal Server Error", 500),
    INVALID_INPUT("C002", "Invalid Input Parameters", 400),
    UNAUTHORIZED("A001", "Authentication Failed", 401),
    FORBIDDEN("A002", "Access Denied", 403),
    USER_NOT_FOUND("U001", "User Not Found", 404),
    DUPLICATE_USER("U002", "User Already Exists", 409);

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
