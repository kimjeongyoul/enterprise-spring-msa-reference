package com.global.auth.order.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.common.domain.OrderStatus;
import com.global.auth.common.event.OrderCreatedEvent;
import com.global.auth.common.event.OrderFailedEvent;
import com.global.auth.order.dto.OrderReadModel;
import com.global.auth.order.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * CQRS 동기화 리스너
 * 이벤트를 받아 조회 전용 DB(Redis)를 실시간으로 최신화
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ObjectMapper objectMapper;
    private final OrderQueryService orderQueryService;

    @KafkaListener(topics = "order-events", groupId = "order-query-group")
    public void handleOrderCreated(String payload) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);
            
            OrderReadModel model = OrderReadModel.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .productId(event.getProductId())
                    .quantity(event.getQuantity())
                    .status(OrderStatus.PENDING)
                    .createdAt(event.getCreatedAt() != null ? event.getCreatedAt() : LocalDateTime.now())
                    .build();
            
            orderQueryService.syncOrder(model);
            
        } catch (Exception e) {
            log.error("Failed to sync read model for created order: {}", e.getMessage());
        }
    }

    // TODO: 주문 취소(FailedEvent) 시에도 Redis 데이터를 '취소' 상태로 업데이트하는 로직 추가 가능
}
