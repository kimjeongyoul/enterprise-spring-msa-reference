package com.global.auth.common.event;

import lombok.*;

/**
 * 전사 표준 주문 실패/취소 이벤트 (Saga 보상 트랜잭션용 Payload)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderFailedEvent {
    private Long orderId;
    private String productId;
    private String reason; // 예: "OUT_OF_STOCK"
}
