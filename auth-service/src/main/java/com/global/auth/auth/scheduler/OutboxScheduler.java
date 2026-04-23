package com.global.auth.auth.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.auth.auth.domain.OutboxEvent;
import com.global.auth.auth.domain.OutboxStatus;
import com.global.auth.auth.repository.OutboxRepository;
import com.global.auth.common.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    /**
     * 5초마다 PENDING 상태인 이벤트를 읽어서 발행
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    @SneakyThrows
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxRepository.findByStatus(OutboxStatus.PENDING);
        
        if (events.isEmpty()) return;

        log.info("[Outbox] Found {} pending events to process", events.size());

        for (OutboxEvent event : events) {
            try {
                // 1. JSON 페이로드를 다시 객체로 변환
                NotificationEvent notification = objectMapper.readValue(event.getPayload(), NotificationEvent.class);
                
                // 2. 이벤트 발행 (메시지 브로커로 던지는 것을 시뮬레이션)
                eventPublisher.publishEvent(notification);
                
                // 3. 완료 처리
                event.markAsProcessed();
                log.info("[Outbox] Successfully processed event ID: {}", event.getId());
            } catch (Exception e) {
                log.error("[Outbox] Failed to process event ID: {}", event.getId(), e);
                // 실제 실무에서는 retry 횟수를 체크하여 FAILED 상태로 넘기는 로직 추가 필요
            }
        }
    }
}
