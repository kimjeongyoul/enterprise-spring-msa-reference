package com.global.auth.common.event;

public interface MessagePublisher {
    void publish(NotificationEvent event);
}
