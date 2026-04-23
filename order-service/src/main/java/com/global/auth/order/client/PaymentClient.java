package com.global.auth.order.client;

import com.global.auth.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment-service", url = "${payment-service.url}")
public interface PaymentClient {
    @GetMapping("/api/v1/payments/status/{orderId}")
    ApiResponse<Boolean> getPaymentStatus(@PathVariable("orderId") Long orderId);
}
