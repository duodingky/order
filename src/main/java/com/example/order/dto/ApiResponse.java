package com.example.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private Map<String, Object> meta = Map.of();

    public ApiResponse(T data) {
        this.data = data;
        this.meta = Map.of();
    }
}
