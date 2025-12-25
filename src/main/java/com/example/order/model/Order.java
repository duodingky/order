package com.example.order.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "order_total")
    private Double orderTotal;

    @Column(name = "item_total")
    private Double itemTotal;

    @Column(name = "shipping_total")
    private Double shippingTotal;

    @Column(name = "payment")
    private String payment;

    @Embedded
    private ShippingDetails shippingDetails;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    public Order() {}

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public LocalDateTime getOrderDate() { return orderDate; }

    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public Double getOrderTotal() { return orderTotal; }

    public void setOrderTotal(Double orderTotal) { this.orderTotal = orderTotal; }

    public Double getItemTotal() { return itemTotal; }

    public void setItemTotal(Double itemTotal) { this.itemTotal = itemTotal; }

    public Double getShippingTotal() { return shippingTotal; }

    public void setShippingTotal(Double shippingTotal) { this.shippingTotal = shippingTotal; }

    public String getPayment() { return payment; }

    public void setPayment(String payment) { this.payment = payment; }

    public ShippingDetails getShippingDetails() { return shippingDetails; }

    public void setShippingDetails(ShippingDetails shippingDetails) { this.shippingDetails = shippingDetails; }

    public List<Item> getItems() { return items; }

    public void setItems(List<Item> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(this::addItem);
        }
    }

    public void addItem(Item item) {
        item.setOrder(this);
        this.items.add(item);
    }
}
