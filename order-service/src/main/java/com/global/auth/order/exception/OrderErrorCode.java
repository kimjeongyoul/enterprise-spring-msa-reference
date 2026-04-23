package com.global.auth.order.exception;

import com.global.auth.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND("O001", "ORDER_NOT_FOUND", 404),
    INSUFFICIENT_STOCK("P001", "INSUFFICIENT_STOCK", 400),
    PAYMENT_FAILED("O002", "PAYMENT_FAILED", 500);

    private final String code;
    private final String messageKey;
    private final int status;
}
