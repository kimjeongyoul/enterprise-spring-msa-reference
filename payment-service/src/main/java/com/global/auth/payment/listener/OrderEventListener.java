package com.global.auth.payment.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.common.event.OrderCreatedEvent;
import com.global.auth.common.event.OrderFailedEvent;
import com.global.auth.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void handleOrderCreated(String payload) {
        OrderCreatedEvent event = null;
        try {
            event = objectMapper.readValue(payload, OrderCreatedEvent.class);
            log.info(">>>> Async Payment Started for Order ID: {}", event.getOrderId());
            
            paymentService.processPayment(event.getOrderId(), event.getUserId(), event.getTotalPrice());
            
        } catch (Exception e) {
            log.error("Payment failed: {}", e.getMessage());
            
            // [Saga 보상] 결제 실패 시 주문 취소 이벤트 발행
            if (event != null) {
                publishCompensationEvent(event.getOrderId(), "PAYMENT_FAILED: " + e.getMessage());
            }
        }
    }

    private void publishCompensationEvent(Long orderId, String reason) {
        try {
            OrderFailedEvent failedEvent = OrderFailedEvent.builder()
                    .orderId(orderId)
                    .reason(reason)
                    .build();
            
            kafkaTemplate.send("order-failed-events", objectMapper.writeValueAsString(failedEvent));
            log.info("<<<< Saga Compensation Event (OrderFailedEvent) published by Payment Service for Order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish compensation event for Order ID: {}", orderId);
        }
    }
}
