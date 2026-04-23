package com.global.auth.order.domain;

import com.global.auth.common.domain.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // Gateway에서 넘겨준 username 저장용

    private String productId;
    
    private String productName; // 주문 시점의 상품명 스냅샷

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 공통 코드 사용

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
