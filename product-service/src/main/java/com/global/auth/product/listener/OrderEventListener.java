package com.global.auth.product.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.common.event.OrderCreatedEvent;
import com.global.auth.common.event.OrderFailedEvent;
import com.global.auth.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 주문 생성 이벤트를 비동기로 처리하여 재고를 차감하는 소비자
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "order-events", groupId = "product-group")
    public void handleOrderCreated(String payload) {
        OrderCreatedEvent event = null;
        try {
            event = objectMapper.readValue(payload, OrderCreatedEvent.class);
            log.info(">>>> Async Stock Reduction Started for Order ID: {} - Product: {}", 
                    event.getOrderId(), event.getProductId());
            
            // 실제 재고 차감 (productId는 현재 String이므로 변환 필요)
            productService.decreaseStock(Long.parseLong(event.getProductId()), event.getQuantity());
            
        } catch (Exception e) {
            log.error("Failed to decrease stock: {}", e.getMessage());
            
            // [Saga 패턴] 재고 차감 실패 시, 보상 트랜잭션(주문 취소) 시작
            if (event != null) {
                publishCompensationEvent(event.getOrderId(), event.getProductId(), "OUT_OF_STOCK: " + e.getMessage());
            }
        }
    }

    private void publishCompensationEvent(Long orderId, String productId, String reason) {
        try {
            OrderFailedEvent failedEvent = OrderFailedEvent.builder()
                    .orderId(orderId)
                    .productId(productId)
                    .reason(reason)
                    .build();
            
            String failedPayload = objectMapper.writeValueAsString(failedEvent);
            kafkaTemplate.send("order-failed-events", failedPayload);
            log.info("<<<< Saga Compensation Event (OrderFailedEvent) published for Order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Critical: Failed to publish compensation event for Order ID: {}", orderId);
        }
    }
}
