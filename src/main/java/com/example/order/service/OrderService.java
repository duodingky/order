package com.example.order.service;

import com.example.order.model.Order;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {
    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public Optional<Order> getOrder(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Order createOrder(Order order) {
        long id = idGen.getAndIncrement();
        Order created = new Order(id, order.getProduct(), order.getQuantity(), order.getPrice());
        store.put(id, created);
        return created;
    }
}
