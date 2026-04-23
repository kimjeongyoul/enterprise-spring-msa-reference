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
import com.global.auth.common.idempotency.IdempotencyManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final IdempotencyManager idempotencyManager;

    @KafkaListener(topics = "order-events", groupId = "product-group")
    public void handleOrderCreated(String payload) {
        OrderCreatedEvent event = null;
        try {
            event = objectMapper.readValue(payload, OrderCreatedEvent.class);
            
            // 멱등성 체크 (고유 키: ORDER_CREATED + orderId)
            String idempotencyKey = "ORDER_CREATED:" + event.getOrderId();
            if (idempotencyManager.isProcessed(idempotencyKey)) {
                log.warn("Duplicate message detected and skipped: {}", idempotencyKey);
                return;
            }

            log.info(">>>> Async Stock Reduction Started for Order ID: {} - Product: {}", 
                    event.getOrderId(), event.getProductId());
            
            // 실제 재고 차감
            productService.decreaseStock(Long.parseLong(event.getProductId()), event.getQuantity());
            
            // 처리 완료 마킹
            idempotencyManager.markAsProcessed(idempotencyKey);
            
        } catch (Exception e) {
            log.error("Failed to decrease stock, triggering Saga compensation and Rethrowing for Retry/DLQ: {}", e.getMessage());
            
            if (event != null) {
                publishCompensationEvent(event.getOrderId(), event.getProductId(), "OUT_OF_STOCK: " + e.getMessage());
            }
            // 예외를 다시 던져서 Kafka ErrorHandler가 감지하게 함
            throw new RuntimeException("Consumer Error: " + e.getMessage(), e);
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
