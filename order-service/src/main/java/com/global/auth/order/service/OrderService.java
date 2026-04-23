package com.global.auth.order.service;

import com.global.auth.order.domain.Order;
import com.global.auth.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(String userId, String productId, Integer quantity) {
        Order order = Order.builder()
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .build();
        return orderRepository.save(order);
    }

    public List<Order> getMyOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
