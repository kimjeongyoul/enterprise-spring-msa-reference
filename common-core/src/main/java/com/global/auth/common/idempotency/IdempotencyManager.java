package com.global.auth.common.idempotency;

/**
 * 메시지 멱등성 보장을 위한 관리 인터페이스
 */
public interface IdempotencyManager {
    /**
     * 키가 이미 처리되었는지 확인하고, 처리되지 않았다면 처리 중으로 표시
     * @param key 고유 키 (예: eventId)
     * @return 이미 처리된 경우 true, 처음 처리하는 경우 false
     */
    boolean isProcessed(String key);

    /**
     * 처리 완료 마킹
     * @param key 고유 키
     */
    void markAsProcessed(String key);
}
