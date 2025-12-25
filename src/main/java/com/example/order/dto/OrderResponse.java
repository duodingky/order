package com.example.order.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long id;
    private LocalDateTime orderDate;
    private Double orderTotal;
    private Double itemTotal;
    private Double shippingTotal;
    private String payment;
    private ShippingDetailsResponse shippingDetails;
    private List<ItemResponse> items;

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
    public ShippingDetailsResponse getShippingDetails() { return shippingDetails; }
    public void setShippingDetails(ShippingDetailsResponse shippingDetails) { this.shippingDetails = shippingDetails; }
    public List<ItemResponse> getItems() { return items; }
    public void setItems(List<ItemResponse> items) { this.items = items; }
}
