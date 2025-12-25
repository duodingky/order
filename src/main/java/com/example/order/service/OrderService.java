package com.example.order.service;

import com.example.order.model.Item;
import com.example.order.model.Order;
import com.example.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> getOrder(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }

        // compute item totals if not provided
        double itemTotal = 0.0;
        if (order.getItems() != null) {
            for (Item it : order.getItems()) {
                if (it.getAmount() == null) {
                    double amt = (it.getUnitPrice() == null ? 0.0 : it.getUnitPrice()) * (it.getQty() == null ? 0 : it.getQty());
                    it.setAmount(amt);
                }
                itemTotal += it.getAmount() == null ? 0.0 : it.getAmount();
            }
        }
        if (order.getItemTotal() == null) order.setItemTotal(itemTotal);

        double shipping = order.getShippingTotal() == null ? 0.0 : order.getShippingTotal();
        order.setOrderTotal((order.getItemTotal() == null ? 0.0 : order.getItemTotal()) + shipping);

        return orderRepository.save(order);
    }
}
