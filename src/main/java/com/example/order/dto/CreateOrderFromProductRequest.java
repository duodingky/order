package com.example.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderFromProductRequest {
    @NotEmpty
    private List<OrderItemRequest> orderItems;

    @Data
    public static class OrderItemRequest {
        @NotBlank
        private String id;

        @NotNull
        @Min(1)
        private Integer qty;
    }
}
