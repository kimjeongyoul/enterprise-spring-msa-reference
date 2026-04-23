package com.global.auth.payment.controller;

import com.global.auth.common.dto.ApiResponse;
import com.global.auth.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/status/{orderId}")
    public ApiResponse<Boolean> getStatus(@PathVariable Long orderId) {
        boolean isCompleted = paymentService.isPaymentCompleted(orderId);
        return ApiResponse.success(isCompleted);
    }
}
