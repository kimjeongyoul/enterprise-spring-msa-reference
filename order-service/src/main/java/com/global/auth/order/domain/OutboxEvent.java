package com.global.auth.order.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_outbox")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aggregateType; // 예: ORDER

    @Column(nullable = false)
    private String eventType;     // 예: ORDER_CREATED

    @Lob
    @Column(nullable = false)
    private String payload;       // JSON 데이터

    @Column(nullable = false)
    private boolean processed;    // 발행 여부

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public void markAsProcessed() {
        this.processed = true;
        this.processedAt = LocalDateTime.now();
    }
}
