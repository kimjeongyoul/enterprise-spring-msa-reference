package com.global.auth.auth.exception;

import com.global.auth.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    USER_NOT_FOUND("U001", "USER_NOT_FOUND", 404),
    DUPLICATE_USER("U002", "DUPLICATE_USER", 409);

    private final String code;
    private final String messageKey;
    private final int status;
}
