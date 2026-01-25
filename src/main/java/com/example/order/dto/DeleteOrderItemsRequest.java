package com.example.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DeleteOrderItemsRequest {
    @NotNull
    @Valid
    private OrderItemsData orderItems;

    @Data
    public static class OrderItemsData {
        @NotEmpty
        private List<Long> ids;
    }
}
