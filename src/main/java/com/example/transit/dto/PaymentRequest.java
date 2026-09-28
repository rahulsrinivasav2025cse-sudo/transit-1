package com.example.transit.dto;

import com.example.transit.entity.enums.PaymentMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull
    private PaymentMode mode; // CASH, BANK, DIGITAL
}
