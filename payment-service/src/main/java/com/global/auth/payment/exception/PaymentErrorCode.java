package com.global.auth.payment.exception;

import com.global.auth.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    INSUFFICIENT_BALANCE("PAY-001", "INSUFFICIENT_BALANCE", 400),
    PAYMENT_GATEWAY_ERROR("PAY-002", "PAYMENT_GATEWAY_ERROR", 500);

    private final String code;
    private final String messageKey;
    private final int status;
}
