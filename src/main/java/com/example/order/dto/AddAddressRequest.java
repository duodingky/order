package com.example.order.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class AddAddressRequest {
    @JsonAlias("first_name")
    private String firstName;

    @JsonAlias("last_name")
    private String lastName;

    private String address1;

    @JsonAlias("address_type")
    private String addressType;

    private String city;

    @JsonAlias("zip_code")
    private String zipCode;

    private String province;

    private String country;
}
