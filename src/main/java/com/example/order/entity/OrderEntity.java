package com.example.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    private String id;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "order_total")
    private Double orderTotal;

    @Column(name = "item_total")
    private Double itemTotal;

    @Column(name = "shipping_total")
    private Double shippingTotal;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "order_status")
    private String orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        if (this.orderDate == null) this.orderDate = LocalDateTime.now();
        if (this.orderStatus == null) this.orderStatus = "submitted";
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.orderItems.add(item);
    }

    public void addAddress(Address address) {
        address.setOrder(this);
        this.addresses.add(address);
    }
}
