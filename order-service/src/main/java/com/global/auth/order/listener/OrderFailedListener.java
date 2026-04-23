package com.global.auth.order.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.common.event.OrderFailedEvent;
import com.global.auth.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 다른 서비스(예: Product)에서 발생한 실패 이벤트를 수신하여
 * 보상 트랜잭션(주문 취소)을 실행하는 Saga 소비자
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFailedListener {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = "order-failed-events", groupId = "order-compensation-group")
    public void handleOrderFailed(String payload) {
        try {
            OrderFailedEvent event = objectMapper.readValue(payload, OrderFailedEvent.class);
            log.warn(">>>> Saga Compensation Started for Order ID: {} - Reason: {}", 
                    event.getOrderId(), event.getReason());
            
            // 보상 트랜잭션 실행: 주문 상태를 '취소'로 변경
            orderService.cancelOrder(event.getOrderId(), event.getReason());
            
        } catch (Exception e) {
            log.error("Critical: Failed to process Saga compensation for order: {}", e.getMessage());
            // 실제 운영에서는 관리자 알림(Slack 등) 전송 필요
        }
    }
}
