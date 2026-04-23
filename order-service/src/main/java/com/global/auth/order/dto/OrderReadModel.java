package com.global.auth.order.dto;

import com.global.auth.common.domain.OrderStatus;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 조회 전용주문 모델 (Read Model for Redis)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderReadModel {
    private Long orderId;
    private String userId;
    private String productId;
    private Integer quantity;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
