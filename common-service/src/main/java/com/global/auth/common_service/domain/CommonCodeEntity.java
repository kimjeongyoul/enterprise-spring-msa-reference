package com.global.auth.common_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "common_codes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String groupCode; // 예: ORDER_STATUS

    @Column(nullable = false)
    private String code;      // 예: CREATED, COMPLETED

    @Column(nullable = false)
    private String name;      // 예: 주문 생성됨

    private String description;
    private Integer sortOrder;
    private boolean isUsed;
}
