package com.global.auth.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 전사 표준 알림 이벤트 규격
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    private String receiver; // 수신자 (Email, Phone 등)
    private String title;    // 알림 제목
    private String content;  // 알림 내용
    private String type;     // 알림 타입 (EMAIL, SMS, PUSH)
}
