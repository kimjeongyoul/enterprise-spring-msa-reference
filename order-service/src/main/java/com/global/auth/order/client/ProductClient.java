package com.global.auth.order.client;

import com.global.auth.common.dto.ApiResponse;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8083")
public interface ProductClient {

    @GetMapping("/api/v1/internal/products/{productId}")
    ApiResponse<ProductResponse> getProduct(@PathVariable("productId") Long productId);

    @Getter
    class ProductResponse {
        private Long id;
        private String name;
        private Long price;
    }
}
