package com.global.auth.auth.event;

import com.global.auth.common.event.MessagePublisher;
import com.global.auth.common.event.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev") // dev 프로파일일 때만 동작
public class KafkaMessagePublisher implements MessagePublisher {
    
    @Override
    public void publish(NotificationEvent event) {
        log.info("[Kafka] (Simulation) Publishing event to Kafka topic: notification-topic");
        // 실제 구현 시: kafkaTemplate.send("notification-topic", event);
    }
}
