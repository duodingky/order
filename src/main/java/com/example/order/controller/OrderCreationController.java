package com.example.order.controller;

import com.example.order.dto.CreateOrderFromProductRequest;
import com.example.order.entity.OrderEntity;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderCreationController {
    private final OrderService orderService;

    public OrderCreationController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderFromProductRequest req) {
        OrderEntity saved = orderService.createOrderFromProductRequest(req);
        return ResponseEntity.ok(saved.getId());
    }
}
