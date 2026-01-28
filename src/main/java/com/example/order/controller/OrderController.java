package com.example.order.controller;

import com.example.order.dto.AddAddressRequest;
import com.example.order.dto.ApiResponse;
import com.example.order.dto.CreateOrderFromProductRequest;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.DeleteOrderItemsRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.entity.Address;
import com.example.order.entity.OrderEntity;
import com.example.order.entity.OrderItem;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest req) {
        OrderEntity saved = orderService.createOrder(req);
        return ResponseEntity.ok(new ApiResponse<>(saved.getId()));
    }

    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrderFromProduct(@Valid @RequestBody CreateOrderFromProductRequest req) {
        OrderEntity saved = orderService.createOrderFromProductRequest(req);
        return ResponseEntity.ok(new ApiResponse<>(saved.getId()));
    }

    @PostMapping("/addItem/{orderId}")
    public ResponseEntity<?> addItem(@PathVariable String orderId,
                                     @Valid @RequestBody CreateOrderFromProductRequest req) {
        OrderEntity saved = orderService.addItemsToOrder(orderId, req);
        return ResponseEntity.ok(new ApiResponse<>(saved.getId()));
    }

    @PostMapping("/addAddress/{orderID}")
    public ResponseEntity<?> addAddress(@PathVariable("orderID") String orderId,
                                        @Valid @RequestBody List<AddAddressRequest> req) {
        OrderEntity saved = orderService.addAddressesToOrder(orderId, req);
        return ResponseEntity.ok(new ApiResponse<>(saved.getId()));
    }

    @PutMapping("/updateAddress/{orderID}")
    public ResponseEntity<?> updateAddress(@PathVariable("orderID") String orderId,
                                           @Valid @RequestBody List<AddAddressRequest> req) {
        OrderEntity saved = orderService.updateAddressesForOrder(orderId, req);
        return ResponseEntity.ok(new ApiResponse<>(saved.getId()));
    }

    @DeleteMapping("/orderItem/{orderId}")
    public ResponseEntity<?> deleteItems(@PathVariable String orderId,
                                         @Valid @RequestBody DeleteOrderItemsRequest req) {
        OrderEntity saved = orderService.removeItemsFromOrder(orderId, req);
        return ResponseEntity.ok(new ApiResponse<>(saved.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id) {
        return orderService.findById(id)
                .map(this::toResponse)
                .<ResponseEntity<?>>map(response -> ResponseEntity.ok(new ApiResponse<>(response)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(Map.of())));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestBody StatusUpdate req) {
        return orderService.updateStatus(id, req.getOrder_status())
                .map(o -> ResponseEntity.ok(new ApiResponse<>(Map.of())))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(Map.of())));
    }

    private OrderResponse toResponse(OrderEntity e) {
        OrderResponse r = new OrderResponse();
        r.setId(e.getId());
        r.setOrderDate(e.getOrderDate());
        r.setOrderTotal(e.getOrderTotal());
        r.setItemTotal(e.getItemTotal());
        r.setShippingTotal(e.getShippingTotal());
        r.setPaymentMethod(e.getPaymentMethod());
        r.setOrderStatus(e.getOrderStatus());
        r.setOrderItems(e.getOrderItems().stream().map(this::mapItem).collect(Collectors.toList()));
        r.setAddresses(e.getAddresses().stream().map(this::mapAddress).collect(Collectors.toList()));
        return r;
    }

    private OrderResponse.OrderItemResponse mapItem(OrderItem i) {
        OrderResponse.OrderItemResponse r = new OrderResponse.OrderItemResponse();
        r.setId(i.getId());
        r.setProductId(i.getProductId());
        r.setSku(i.getSku());
        r.setCategoryId(i.getCategoryId());
        r.setCategoryName(i.getCategoryName());
        r.setBrandId(i.getBrandId());
        r.setBrandName(i.getBrandName());
        r.setUnitPrice(i.getUnitPrice());
        r.setPrice(i.getPrice() != null ? i.getPrice() : i.getUnitPrice());
        r.setQuantity(i.getQuantity());
        r.setProductName(i.getProductName());
        r.setShortDesc(i.getShortDesc());
        r.setAmount(i.getAmount());
        return r;
    }

    private OrderResponse.AddressResponse mapAddress(Address a) {
        OrderResponse.AddressResponse r = new OrderResponse.AddressResponse();
        r.setId(a.getId());
        r.setAddressType(a.getAddressType() != null ? a.getAddressType().name() : null);
        r.setFirstName(a.getFirstName());
        r.setLastName(a.getLastName());
        r.setCountry(a.getCountry());
        r.setCity(a.getCity());
        r.setProvince(a.getProvince());
        r.setZipCode(a.getZipCode());
        r.setAddress1(a.getAddress1());
        return r;
    }

    public static class StatusUpdate {
        private String order_status;

        public String getOrder_status() {
            return order_status;
        }

        public void setOrder_status(String order_status) {
            this.order_status = order_status;
        }
    }
}
