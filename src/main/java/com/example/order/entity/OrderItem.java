package com.example.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    private String sku;

    @Column(name = "unit_price")
    private Double unitPrice;

    private Integer quantity;

    @Column(name = "product_name")
    private String productName;

    private Double amount;

    @PrePersist
    @PreUpdate
    public void computeAmount() {
        if (this.unitPrice != null && this.quantity != null) {
            this.amount = this.unitPrice * this.quantity;
        }
    }
}
