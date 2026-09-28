package com.example.transit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseOrderRequest {

    @NotNull
    private Long vendorId;

    @NotNull
    private Long productId;

    @Positive
    private Integer quantity;

    // Optional - defaults to the product's unit price when omitted
    private BigDecimal unitPrice;

    private LocalDate orderDate;
}
