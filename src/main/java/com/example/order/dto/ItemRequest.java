package com.example.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ItemRequest {
    @NotBlank
    private String sku;

    @NotNull
    @Min(0)
    private Double unitPrice;

    @NotBlank
    private String productName;

    @NotNull
    @Min(1)
    private Integer qty;

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
}
