package com.global.auth.order.scheduler;

import com.global.auth.order.domain.OutboxEvent;
import com.global.auth.order.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 아웃박스에 쌓인 미발행 이벤트를 Kafka로 전송하는 스케줄러
 * 부하 폭주 시에도 DB 트랜잭션과 독립적으로 Kafka로 이벤트를 쏴줌
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 500) // 0.5초마다 실행
    @Transactional
    public void publishEvents() {
        List<OutboxEvent> events = outboxRepository.findByProcessedFalse();
        
        for (OutboxEvent event : events) {
            try {
                // Kafka 전송 (토픽명: order-events)
                kafkaTemplate.send("order-events", event.getPayload());
                
                // 전송 성공 시 처리 완료 마킹
                event.markAsProcessed();
                log.info("Published outbox event to Kafka: {}", event.getId());
                
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}: {}", event.getId(), e.getMessage());
            }
        }
    }
}
