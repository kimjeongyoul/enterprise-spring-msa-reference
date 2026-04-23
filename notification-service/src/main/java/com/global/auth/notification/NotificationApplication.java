package com.global.auth.notification;

import com.global.auth.common.event.NotificationEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@SpringBootApplication
@EnableAsync // 비동기 처리 활성화
@ComponentScan(basePackages = {"com.global.auth.notification", "com.global.auth.common"})
public class NotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
}

@Service
class NotificationListener {

    /**
     * 시스템 전체에서 발생하는 알림 이벤트를 비동기로 수신
     */
    @EventListener
    @org.springframework.scheduling.annotation.Async
    public void handleNotification(NotificationEvent event) {
        System.out.println("[Notification Service] Sending " + event.getType() + " to " + event.getReceiver());
        System.out.println("Title: " + event.getTitle());
        System.out.println("Content: " + event.getContent());
        // 실제로는 외부 API(Mailgun, Firebase 등)를 호출하여 발송함.
    }
}
