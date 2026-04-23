package com.global.auth.auth.event;

import com.global.auth.common.event.MessagePublisher;
import com.global.auth.common.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!dev") // dev가 아닐 때 (로컬) 동작
@RequiredArgsConstructor
public class LocalMessagePublisher implements MessagePublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(NotificationEvent event) {
        log.info("[Local] Publishing event via Spring ApplicationEvent");
        eventPublisher.publishEvent(event);
    }
}
