package com.global.auth.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType; // 예: USER
    
    private String eventType;    // 예: SIGNUP_SUCCESS
    
    @Column(columnDefinition = "TEXT")
    private String payload;      // JSON으로 변환된 이벤트 데이터

    @Enumerated(EnumType.STRING)
    private OutboxStatus status; // PENDING, PROCESSED, FAILED

    private LocalDateTime createdAt;
    
    private LocalDateTime processedAt;

    public void markAsProcessed() {
        this.status = OutboxStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.status = OutboxStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
