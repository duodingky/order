package com.example.order.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private String id;
    private LocalDateTime orderDate;
    private Double orderTotal;
    private Double itemTotal;
    private Double shippingTotal;
    private String paymentMethod;
    private String orderStatus;
    private List<OrderItemResponse> orderItems;
    private List<AddressResponse> addresses;

    @Data
    public static class OrderItemResponse {
        private Long id;
        private String sku;
        private Double unitPrice;
        private Integer quantity;
        private String productName;
        private Double amount;
    }

    @Data
    public static class AddressResponse {
        private Long id;
        private String addressType;
        private String firstName;
        private String lastName;
        private String country;
        private String city;
        private String zipCode;
        private String address1;
    }
}
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
