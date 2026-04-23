package com.global.auth.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.order.dto.OrderReadModel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String KEY_PREFIX = "orders:user:";

    @SneakyThrows
    public void syncOrder(OrderReadModel model) {
        String key = KEY_PREFIX + model.getUserId();
        String value = objectMapper.writeValueAsString(model);
        
        // Redis List에 저장 (최근 주문이 앞으로 오도록)
        redisTemplate.opsForList().leftPush(key, value);
        log.info("Synced order to Read Model (Redis): {}", model.getOrderId());
    }

    @SneakyThrows
    public List<OrderReadModel> getOrdersFromCache(String userId) {
        String key = KEY_PREFIX + userId;
        List<String> rawOrders = redisTemplate.opsForList().range(key, 0, -1);
        
        if (rawOrders == null) return List.of();

        return rawOrders.stream()
                .map(raw -> {
                    try {
                        return objectMapper.readValue(raw, OrderReadModel.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }
}
