package com.example.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public class OrderRequest {
    @Valid
    @NotNull
    private ShippingDetailsRequest shippingDetails;

    @PositiveOrZero
    private Double shippingTotal;

    @NotEmpty
    @Valid
    private List<ItemRequest> items;

    @NotNull
    private String payment;

    public ShippingDetailsRequest getShippingDetails() { return shippingDetails; }
    public void setShippingDetails(ShippingDetailsRequest shippingDetails) { this.shippingDetails = shippingDetails; }
    public Double getShippingTotal() { return shippingTotal; }
    public void setShippingTotal(Double shippingTotal) { this.shippingTotal = shippingTotal; }
    public List<ItemRequest> getItems() { return items; }
    public void setItems(List<ItemRequest> items) { this.items = items; }
    public String getPayment() { return payment; }
    public void setPayment(String payment) { this.payment = payment; }
}
