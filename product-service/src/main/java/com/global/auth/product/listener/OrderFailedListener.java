package com.global.auth.product.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.common.event.OrderFailedEvent;
import com.global.auth.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 결제 실패 등으로 인해 주문이 취소된 경우, 차감했던 재고를 원복하는 Saga 소비자
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFailedListener {

    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @KafkaListener(topics = "order-failed-events", groupId = "product-compensation-group")
    public void handleOrderFailed(String payload) {
        try {
            OrderFailedEvent event = objectMapper.readValue(payload, OrderFailedEvent.class);
            log.warn(">>>> Saga Compensation: Restoring Stock for Product ID: {}", event.getProductId());
            
            // 재고 복구 (이전 주문에서 사용했던 productId와 수량을 알아야 함)
            // 현재 OrderFailedEvent에 quantity가 누락되어 있으므로, 실제 운영에서는 이를 포함하거나 
            // 주문 서비스를 다시 조회해야 함. 일단 구조만 잡음.
            if (event.getProductId() != null) {
                productService.restoreStock(Long.parseLong(event.getProductId()), 0); // 수량은 예시로 0 처리
            }
            
        } catch (Exception e) {
            log.error("Failed to restore stock: {}", e.getMessage());
        }
    }
}
