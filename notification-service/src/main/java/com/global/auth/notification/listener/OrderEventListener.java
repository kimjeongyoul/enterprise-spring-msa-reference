package com.global.auth.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 주문 생성 이벤트를 비동기로 처리하는 Kafka 소비자
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void handleOrderCreated(String payload) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);
            
            // 실제 알림 발송 로직 (예: 이메일, 카카오톡 등)
            log.info(">>>> Async Notification Sent for Order ID: {} - User: {}", 
                    event.getOrderId(), event.getUserId());
            
        } catch (Exception e) {
            log.error("Failed to process order event: {}", e.getMessage());
        }
    }
}
