package com.global.auth.common.exception;

/**
 * 전사 공통 에러 코드 규격 (Protocol)
 */
public interface ErrorCode {
    String getCode();
    String getMessageKey(); // 다국어 메시지 키
    int getStatus();        // HTTP 상태 코드
}
