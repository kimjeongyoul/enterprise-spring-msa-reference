package com.global.auth.order.controller;

import com.global.auth.common.dto.ApiResponse;
import com.global.auth.order.domain.Order;
import com.global.auth.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ApiResponse<Order> createOrder(
            @RequestHeader("X-User-Name") String username,
            @RequestBody OrderRequest request) {
        Order order = orderService.createOrder(username, request.getProductId(), request.getQuantity());
        return ApiResponse.success(order);
    }

    @GetMapping
    public ApiResponse<List<Order>> getMyOrders(@RequestHeader("X-User-Name") String username) {
        List<Order> orders = orderService.getMyOrders(username);
        return ApiResponse.success(orders);
    }

    @lombok.Data
    public static class OrderRequest {
        private String productId;
        private Integer quantity;
    }
}
