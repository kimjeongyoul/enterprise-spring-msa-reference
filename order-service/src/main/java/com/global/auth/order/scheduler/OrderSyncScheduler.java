package com.global.auth.order.scheduler;

import com.global.auth.common.domain.OrderStatus;
import com.global.auth.common.dto.ApiResponse;
import com.global.auth.order.client.PaymentClient;
import com.global.auth.order.domain.Order;
import com.global.auth.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 메시지 유실에 대비한 주문 상태 강제 동기화 스케줄러 (Polling)
 */
@Slf4j
//@Component // 테스트를 위해 잠시 비활성화
@RequiredArgsConstructor
public class OrderSyncScheduler {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    @Transactional
    public void syncOrderStatuses() {
        // 생성된 지 5분이 지났는데 아직 PENDING인 주문 조회
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(5);
        List<Order> stuckOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, timeoutThreshold);

        for (Order order : stuckOrders) {
            try {
                ApiResponse<Boolean> response = paymentClient.getPaymentStatus(order.getId());
                
                if (response.isSuccess() && response.getData()) {
                    // 결제 완료된 경우 상태 업데이트
                    order.updateStatus(OrderStatus.COMPLETED);
                    log.info("Order {} status synced to COMPLETED via Polling", order.getId());
                } else {
                    // 결제가 안된 경우 (실패 메시지 유실 가능성) 취소 처리 등 정책 결정 필요
                    log.warn("Order {} still PENDING in payment service. Monitoring continues...", order.getId());
                }
            } catch (Exception e) {
                log.error("Failed to sync status for order {}: {}", order.getId(), e.getMessage());
            }
        }
    }
}
