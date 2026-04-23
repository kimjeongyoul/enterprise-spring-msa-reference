package com.global.auth.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentService {

    @Transactional
    public void processPayment(Long orderId, String userId, Long amount) {
        log.info(">>>> Processing payment for Order ID: {} - User: {} - Amount: {}", orderId, userId, amount);
        
        // [Saga Test] 특정 금액 초과 시 결제 실패 시뮬레이션
        if (amount != null && amount > 1000000) {
            throw new RuntimeException("Payment Limit Exceeded");
        }
        
        log.info("Payment success for Order ID: {}", orderId);
    }
}
