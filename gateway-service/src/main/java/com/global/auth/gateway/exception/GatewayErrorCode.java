package com.global.auth.gateway.exception;

import com.global.auth.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GatewayErrorCode implements ErrorCode {
    UNAUTHORIZED("GW-001", "UNAUTHORIZED_ACCESS", 401),
    FORBIDDEN("GW-002", "ACCESS_DENIED", 403),
    SERVICE_UNAVAILABLE("GW-003", "SERVICE_TEMPORARILY_UNAVAILABLE", 503);

    private final String code;
    private final String messageKey;
    private final int status;
}
