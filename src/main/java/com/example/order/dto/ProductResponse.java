package com.example.order.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {
    private String id;

    @JsonAlias({"name"})
    private String productName;

    private String sku;
    private String categoryId;
    private String categoryName;
    private String brandId;
    private String brandName;
    private Double price;

    @JsonAlias({"shortDescription", "short_desc"})
    private String shortDesc;
}
