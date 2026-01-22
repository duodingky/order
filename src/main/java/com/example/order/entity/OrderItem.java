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

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "brand_id")
    private String brandId;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "unit_price")
    private Double unitPrice;

    private Double price;

    private Integer quantity;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "short_desc")
    private String shortDesc;

    private Double amount;

    @PrePersist
    @PreUpdate
    public void computeAmount() {
        Double effectivePrice = this.price != null ? this.price : this.unitPrice;
        if (effectivePrice != null && this.quantity != null) {
            this.amount = effectivePrice * this.quantity;
        }
    }
}
