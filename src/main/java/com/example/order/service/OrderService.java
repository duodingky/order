package com.example.order.service;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.entity.Address;
import com.example.order.entity.AddressType;
import com.example.order.entity.OrderEntity;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderEntity createOrder(CreateOrderRequest req) {
        OrderEntity order = new OrderEntity();
        if (req.getOrder() != null) {
            order.setOrderTotal(req.getOrder().getOrderTotal());
            order.setItemTotal(req.getOrder().getItemTotal());
            order.setShippingTotal(req.getOrder().getShippingTotal());
            order.setPaymentMethod(req.getOrder().getPaymentMethod());
        }

        if (req.getOrderItems() != null) {
            for (CreateOrderRequest.OrderItemData it : req.getOrderItems()) {
                OrderItem item = new OrderItem();
                item.setSku(it.getSku());
                item.setUnitPrice(it.getUnitPrice());
                item.setQuantity(it.getQty());
                item.setProductName(it.getProductName());
                order.addItem(item);
            }
        }

        if (req.getAddress() != null) {
            for (CreateOrderRequest.AddressData a : req.getAddress()) {
                Address addr = new Address();
                addr.setFirstName(a.getFirstName());
                addr.setLastName(a.getLastName());
                addr.setAddress1(a.getAddress1());
                addr.setCity(a.getCity());
                addr.setCountry(a.getCountry());
                addr.setZipCode(a.getZipCode());
                try {
                    addr.setAddressType(AddressType.valueOf(a.getAddressType()));
                } catch (Exception ex) {
                    // try lowercase
                    addr.setAddressType(AddressType.valueOf(a.getAddressType().toLowerCase()));
                }
                order.addAddress(addr);
            }
        }

        return orderRepository.save(order);
    }

    public Optional<OrderEntity> findById(String id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Optional<OrderEntity> updateStatus(String id, String status) {
        Optional<OrderEntity> o = orderRepository.findById(id);
        o.ifPresent(order -> {
            order.setOrderStatus(status);
            orderRepository.save(order);
        });
        return o;
    }
}
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
