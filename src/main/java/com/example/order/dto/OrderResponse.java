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
        private String categoryId;
        private String categoryName;
        private String brandId;
        private String brandName;
        private Double unitPrice;
        private Double price;
        private Integer quantity;
        private String productName;
        private String shortDesc;
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
