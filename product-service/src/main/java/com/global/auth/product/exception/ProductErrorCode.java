package com.global.auth.product.exception;

import com.global.auth.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND("P001", "PRODUCT_NOT_FOUND", 404),
    OUT_OF_STOCK("P002", "OUT_OF_STOCK", 400);

    private final String code;
    private final String messageKey;
    private final int status;
}
