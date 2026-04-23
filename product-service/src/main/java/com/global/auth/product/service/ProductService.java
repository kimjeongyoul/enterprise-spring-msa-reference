package com.global.auth.product.service;

import com.global.auth.product.domain.Product;
import com.global.auth.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> decreaseStockScript;

    private static final String STOCK_KEY_PREFIX = "product:stock:";

    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        String stockKey = STOCK_KEY_PREFIX + productId;

        // 1. Redis Lua Script를 통한 원자적 선차감 (Atomic Pre-check & Decrease)
        Long remainingStock = redisTemplate.execute(
                decreaseStockScript,
                Collections.singletonList(stockKey),
                String.valueOf(quantity)
        );

        if (remainingStock == null || remainingStock < 0) {
            log.warn("Fast-fail: Out of stock in Redis for product: {}", productId);
            throw new RuntimeException("Out of stock (Redis): " + productId);
        }

        // 2. Redis 성공 시에만 DB 업데이트
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        
        product.decreaseStock(quantity);
        log.info("Stock decreased successfully. Redis remains: {}, DB updated for product: {}", remainingStock, productId);
    }

    @Transactional
    public void restoreStock(Long productId, int quantity) {
        String stockKey = STOCK_KEY_PREFIX + productId;

        // 1. Redis 재고 복구
        redisTemplate.opsForValue().increment(stockKey, quantity);

        // 2. DB 재고 복구
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        
        product.restoreStock(quantity);
        log.info("Stock restored for product {}: added {}", productId, quantity);
    }
}
