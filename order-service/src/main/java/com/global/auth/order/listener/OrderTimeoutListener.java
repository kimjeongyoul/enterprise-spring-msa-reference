package com.global.auth.order.listener;

import com.global.auth.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Redis 키 만료 이벤트를 수신하여 미결제 주문을 자동 취소하는 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutListener implements MessageListener {

    private final RedisMessageListenerContainer listenerContainer;
    private final OrderService orderService;

    @PostConstruct
    public void init() {
        // __keyevent@0__:expired 패턴 구독
        listenerContainer.addMessageListener(this, new PatternTopic("__keyevent@*__:expired"));
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        
        // 키 형식: order:timeout:{orderId}
        if (expiredKey.startsWith("order:timeout:")) {
            String orderIdStr = expiredKey.replace("order:timeout:", "");
            Long orderId = Long.parseLong(orderIdStr);
            
            log.info(">>>> Order Timeout Detected for ID: {}. Starting auto-cancellation...", orderId);
            
            try {
                // 주문 취소 로직 실행 (상태 체크 후 취소)
                orderService.cancelOrder(orderId, "PAYMENT_TIMEOUT");
            } catch (Exception e) {
                log.error("Failed to auto-cancel order {}: {}", orderId, e.getMessage());
            }
        }
    }
}
