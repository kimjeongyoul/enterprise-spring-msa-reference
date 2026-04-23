package com.global.auth.product.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long price;

    private String category;

    private Integer stock; // 재고 수량

    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new RuntimeException("Out of stock for product: " + this.id);
        }
        this.stock -= quantity;
    }

    public void restoreStock(int quantity) {
        this.stock += quantity;
    }
}
