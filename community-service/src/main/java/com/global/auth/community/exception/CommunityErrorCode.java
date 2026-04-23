package com.global.auth.community.exception;

import com.global.auth.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements ErrorCode {
    REVIEW_NOT_FOUND("COM-001", "REVIEW_NOT_FOUND", 404),
    FILE_UPLOAD_ERROR("COM-002", "FILE_UPLOAD_ERROR", 500);

    private final String code;
    private final String messageKey;
    private final int status;
}
