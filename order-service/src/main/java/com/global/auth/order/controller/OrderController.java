package com.global.auth.order.controller;

import com.global.auth.common.dto.ApiResponse;
import com.global.auth.order.domain.Order;
import com.global.auth.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.global.auth.order.dto.OrderReadModel;
import com.global.auth.order.service.OrderQueryService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderQueryService orderQueryService;

    @PostMapping
    public ApiResponse<Order> createOrder(
            @RequestHeader("X-User-Name") String username,
            @RequestBody OrderRequest request) {
        log.info(">>>> [API] Create Order Request: User={}, Product={}", username, request.getProductId());
        Order order = orderService.createOrder(username, request.getProductId(), request.getQuantity());
        return ApiResponse.success(order);
    }

    @GetMapping("/my")
    public ApiResponse<List<OrderReadModel>> getMyOrders(@RequestHeader("X-User-Name") String username) {
        // DB 대신 Redis 조회 모델에서 가져옴
        List<OrderReadModel> orders = orderQueryService.getOrdersFromCache(username);
        return ApiResponse.success(orders);
    }

    @lombok.Data
    public static class OrderRequest {
        private String productId;
        private Integer quantity;
    }
}
