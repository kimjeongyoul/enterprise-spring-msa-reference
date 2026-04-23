package com.global.auth.product.controller;

import com.global.auth.common.dto.ApiResponse;
import com.global.auth.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/products")
@RequiredArgsConstructor
public class InternalProductController {

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
        // 실제로는 DB에서 조회하지만, 테스트를 위해 가상 데이터를 반환합니다.
        return ApiResponse.success(new ProductResponse(productId, "Real Product Name from DB", 1500000L));
    }

    @lombok.Value
    public static class ProductResponse {
        Long id;
        String name;
        Long price;
    }
}
