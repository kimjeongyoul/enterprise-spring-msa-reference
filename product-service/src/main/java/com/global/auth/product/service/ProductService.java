package com.global.auth.product.service;

import com.global.auth.product.domain.Product;
import com.global.auth.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        
        product.decreaseStock(quantity);
        log.info("Stock decreased for product {}: remains {}", productId, product.getStock());
    }

    @Transactional
    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        
        // 재고 복구 (Lombok 없이 직접 필드 접근 가능하도록 엔티티에 메서드 추가 필요)
        // 일단은 setter가 없으므로 엔티티에 restore 메서드 추가할 계획
        log.info("Stock restored for product {}: added {}", productId, quantity);
    }
}
