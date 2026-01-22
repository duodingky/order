package com.example.order.service;

import com.example.order.dto.CreateOrderFromProductRequest;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.ProductResponse;
import com.example.order.entity.Address;
import com.example.order.entity.AddressType;
import com.example.order.entity.OrderEntity;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final String productServiceBaseUrl;

    public OrderService(
            OrderRepository orderRepository,
            RestTemplateBuilder restTemplateBuilder,
            @Value("${product.service.base-url:http://localhost:81}") String productServiceBaseUrl
    ) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplateBuilder.build();
        this.productServiceBaseUrl = normalizeBaseUrl(productServiceBaseUrl);
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
                Double effectivePrice = it.getPrice() != null ? it.getPrice() : it.getUnitPrice();
                item.setUnitPrice(it.getUnitPrice() != null ? it.getUnitPrice() : it.getPrice());
                item.setPrice(effectivePrice);
                item.setQuantity(it.getQty());
                item.setProductName(it.getProductName());
                item.setCategoryId(it.getCategoryId());
                item.setCategoryName(it.getCategoryName());
                item.setBrandId(it.getBrandId());
                item.setBrandName(it.getBrandName());
                item.setShortDesc(it.getShortDesc());
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

    @Transactional
    public OrderEntity createOrderFromProductRequest(CreateOrderFromProductRequest req) {
        OrderEntity order = new OrderEntity();
        List<OrderItem> items = new ArrayList<>();
        double itemTotal = 0;
        boolean hasPrice = false;

        if (req.getOrderItems() != null) {
            for (CreateOrderFromProductRequest.OrderItemRequest it : req.getOrderItems()) {
                ProductResponse product = fetchProduct(it.getId());

                OrderItem item = new OrderItem();
                item.setSku(product.getSku());
                item.setProductName(product.getProductName());
                item.setCategoryId(product.getCategoryId());
                item.setCategoryName(product.getCategoryName());
                item.setBrandId(product.getBrandId());
                item.setBrandName(product.getBrandName());
                item.setPrice(product.getPrice());
                item.setUnitPrice(product.getPrice());
                item.setShortDesc(product.getShortDesc());
                item.setQuantity(it.getQty());
                items.add(item);

                if (product.getPrice() != null && it.getQty() != null) {
                    itemTotal += product.getPrice() * it.getQty();
                    hasPrice = true;
                }
            }
        }

        for (OrderItem item : items) {
            order.addItem(item);
        }

        if (hasPrice) {
            order.setItemTotal(itemTotal);
            order.setOrderTotal(itemTotal);
            order.setShippingTotal(0.0);
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

    private ProductResponse fetchProduct(String productId) {
        String url = productServiceBaseUrl + "/products/" + productId;
        try {
            ProductResponse product = restTemplate.getForObject(url, ProductResponse.class);
            if (product == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Product service returned empty response for id " + productId
                );
            }
            return product;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId, ex);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Product service error for id " + productId,
                    ex
            );
        }
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "http://localhost:81";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
