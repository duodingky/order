package com.example.order.mapper;

import com.example.order.dto.*;
import com.example.order.model.Item;
import com.example.order.model.Order;
import com.example.order.model.ShippingDetails;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static Order toEntity(OrderRequest req) {
        Order order = new Order();
        order.setPayment(req.getPayment());
        order.setShippingTotal(req.getShippingTotal());

        ShippingDetails sd = new ShippingDetails();
        if (req.getShippingDetails() != null) {
            sd.setFirstName(req.getShippingDetails().getFirstName());
            sd.setLastName(req.getShippingDetails().getLastName());
            sd.setAddress1(req.getShippingDetails().getAddress1());
            sd.setCity(req.getShippingDetails().getCity());
            sd.setCountry(req.getShippingDetails().getCountry());
            sd.setZipCode(req.getShippingDetails().getZipCode());
            order.setShippingDetails(sd);
        }

        if (req.getItems() != null) {
            for (ItemRequest ir : req.getItems()) {
                Item it = new Item();
                it.setSku(ir.getSku());
                it.setUnitPrice(ir.getUnitPrice());
                it.setProductName(ir.getProductName());
                it.setQty(ir.getQty());
                order.addItem(it);
            }
        }

        return order;
    }

    public static OrderResponse toResponse(Order order) {
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setOrderDate(order.getOrderDate());
        r.setOrderTotal(order.getOrderTotal());
        r.setItemTotal(order.getItemTotal());
        r.setShippingTotal(order.getShippingTotal());
        r.setPayment(order.getPayment());

        if (order.getShippingDetails() != null) {
            ShippingDetailsResponse s = new ShippingDetailsResponse();
            s.setFirstName(order.getShippingDetails().getFirstName());
            s.setLastName(order.getShippingDetails().getLastName());
            s.setAddress1(order.getShippingDetails().getAddress1());
            s.setCity(order.getShippingDetails().getCity());
            s.setCountry(order.getShippingDetails().getCountry());
            s.setZipCode(order.getShippingDetails().getZipCode());
            r.setShippingDetails(s);
        }

        if (order.getItems() != null) {
            List<ItemResponse> items = order.getItems().stream().map(it -> {
                ItemResponse ir = new ItemResponse();
                ir.setId(it.getId());
                ir.setSku(it.getSku());
                ir.setUnitPrice(it.getUnitPrice());
                ir.setProductName(it.getProductName());
                ir.setQty(it.getQty());
                ir.setAmount(it.getAmount());
                return ir;
            }).collect(Collectors.toList());
            r.setItems(items);
        }

        return r;
    }
}
