package com.global.auth.common.event;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 전사 표준 주문 생성 이벤트 (Kafka Payload)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderCreatedEvent {
    private Long orderId;
    private String userId;
    private String productId;
    private Integer quantity;
    private Long totalPrice;
    private LocalDateTime createdAt;
}
