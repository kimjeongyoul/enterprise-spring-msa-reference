package com.global.auth.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 전사 공통 에러 코드 (Infrastructure/System layer)
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR("C001", "INTERNAL_SERVER_ERROR", 500),
    INVALID_INPUT("C002", "INVALID_INPUT", 400),
    UNAUTHORIZED("A001", "UNAUTHORIZED", 401),
    FORBIDDEN("A002", "FORBIDDEN", 403);

    private final String code;
    private final String messageKey;
    private final int status;
}
