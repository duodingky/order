package com.example.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull
    private OrderData order;

    private List<AddressData> address;

    private List<OrderItemData> orderItems;

    @Data
    public static class OrderData {
        private Double orderTotal;
        private Double itemTotal;
        private Double shippingTotal;
        private String paymentMethod;
    }

    @Data
    public static class AddressData {
        private String firstName;
        private String lastName;
        private String address1;
        private String city;
        private String country;
        private String zipCode;
        private String addressType; // shipping | billing
    }

    @Data
    public static class OrderItemData {
        private String sku;
        private Double unitPrice;
        private Double price;
        private String productName;
        private String categoryId;
        private String categoryName;
        private String brandId;
        private String brandName;
        private String shortDesc;
        private Integer qty;
    }
}
