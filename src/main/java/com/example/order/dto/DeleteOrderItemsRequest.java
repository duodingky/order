package com.example.order.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteOrderItemsRequest {
    @NotEmpty
    private OrderItemsData orderItems;

    @Data
    public static class OrderItemsData {
        @NotEmpty
        private List<Long> ids;
    }
}
