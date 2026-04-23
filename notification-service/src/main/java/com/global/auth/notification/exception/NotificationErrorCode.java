package com.global.auth.notification.exception;

import com.global.auth.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    NOTIFICATION_SEND_FAILED("NOTI-001", "NOTIFICATION_SEND_FAILED", 500);

    private final String code;
    private final String messageKey;
    private final int status;
}
