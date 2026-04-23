package com.global.auth.order.service;

import com.global.auth.common.domain.OrderStatus;
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
        // 실제로는 productService에서 정보를 조회해오겠지만, 현재는 스냅샷 시연을 위해 가상의 상품명을 저장합니다.
        String currentProductName = "Best Laptop - " + productId; 

        Order order = Order.builder()
                .userId(userId)
                .productId(productId)
                .productName(currentProductName)
                .quantity(quantity)
                .status(OrderStatus.PENDING)
                .build();
        return orderRepository.save(order);
    }

    public List<Order> getMyOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
