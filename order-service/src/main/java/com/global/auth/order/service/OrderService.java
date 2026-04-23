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

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderCommonCodeClient commonCodeClient;
    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @SneakyThrows
    public Order createOrder(String username, String productId, int quantity) {
        // [Example] common-service에서 주문 상태값 조회
        List<CommonCodeResponse> orderStatuses = commonCodeClient.getCodesByGroup("ORDER_STATUS");
        log.info("Current Order Status Codes: {}", orderStatuses);

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
    }

    @Transactional(readOnly = true)
    public List<Order> getMyOrders(String username) {
        return orderRepository.findByUserId(username);
    }

    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
        
        // 상태를 취소로 변경 (Saga 보상 트랜잭션 결과)
        order = Order.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .status(OrderStatus.CANCELLED)
                .build();
        
        orderRepository.save(order);
        log.warn("Saga Compensation Applied: Order {} CANCELLED. Reason: {}", orderId, reason);
    }
}
