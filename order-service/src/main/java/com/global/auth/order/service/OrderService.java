package com.global.auth.order.service;

import com.global.auth.common.exception.CustomException;
import com.global.auth.order.client.OrderCommonCodeClient;
import com.global.auth.order.exception.OrderErrorCode;
import com.global.auth.common.dto.CommonCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.global.auth.order.domain.Order;
import com.global.auth.order.repository.OrderRepository;
import java.util.ArrayList;

import com.global.auth.common.domain.OrderStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.common.event.OrderCreatedEvent;
import com.global.auth.order.domain.OutboxEvent;
import java.time.LocalDateTime;

import com.global.auth.order.repository.OutboxRepository;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderCommonCodeClient commonCodeClient;
    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    private final StringRedisTemplate redisTemplate;
    private static final String QUEUE_KEY = "order:processing:count";
    private static final String TIMEOUT_KEY_PREFIX = "order:timeout:";
    private static final int MAX_CONCURRENT_ORDERS = 100;
    private static final Duration TEST_TIMEOUT = Duration.ofSeconds(60);

    @Transactional
    @SneakyThrows
    public Order createOrder(String username, String productId, int quantity) {
        // [Advanced] 1. 대기열/진입 제어 체크
        Long currentProcessing = redisTemplate.opsForValue().increment(QUEUE_KEY);
        try {
            if (currentProcessing != null && currentProcessing > MAX_CONCURRENT_ORDERS) {
                log.warn("System overloaded: Current active orders: {}", currentProcessing);
                throw new CustomException(OrderErrorCode.PAYMENT_FAILED);
            }

            log.info("Skipping common-service call for testing Saga flow...");

            if (quantity <= 0) {
                throw new CustomException(OrderErrorCode.INSUFFICIENT_STOCK);
            }

            // 1. 주문 저장
            Order order = Order.builder()
                    .userId(username)
                    .productId(productId)
                    .quantity(quantity)
                    .status(OrderStatus.PENDING)
                    .build();
            Order savedOrder = orderRepository.save(order);

            // [Advanced] 1.5. Redis 타임아웃 키 설정 (60초 뒤 만료 이벤트 발생)
            redisTemplate.opsForValue().set(
                    TIMEOUT_KEY_PREFIX + savedOrder.getId(), 
                    "pending", 
                    TEST_TIMEOUT
            );

            // 2. 아웃박스 이벤트 저장 (원자성 보장)
            OrderCreatedEvent event = OrderCreatedEvent.builder()
                    .orderId(savedOrder.getId())
                    .userId(username)
                    .productId(productId)
                    .quantity(quantity)
                    .createdAt(LocalDateTime.now())
                    .build();

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType("ORDER")
                    .eventType("ORDER_CREATED")
                    .payload(objectMapper.writeValueAsString(event))
                    .processed(false)
                    .createdAt(LocalDateTime.now())
                    .build();            
            outboxRepository.save(outboxEvent);

            log.info("Order and OutboxEvent saved successfully for ID: {}", savedOrder.getId());
            return savedOrder;
        } finally {
            redisTemplate.opsForValue().decrement(QUEUE_KEY);
        }
    }

    @Transactional(readOnly = true)
    public List<Order> getMyOrders(String username) {
        return orderRepository.findByUserId(username);
    }

    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
        
        // [Advanced] 이미 완료되거나 취소된 주문은 처리하지 않음
        if (order.getStatus() != OrderStatus.PENDING) {
            log.info("Order {} is already {}, skipping cancellation.", orderId, order.getStatus());
            return;
        }

        // 상태를 취소로 변경
        order.updateStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        log.warn("Saga Compensation Applied: Order {} CANCELLED. Reason: {}", orderId, reason);
    }
}
