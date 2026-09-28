package com.example.transit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BudgetRequest {

    private String name;

    @NotNull
    private Long analyticAccountId;

    @NotNull
    private BigDecimal plannedAmount;

    @NotNull
    private LocalDate periodStart;

    @NotNull
    private LocalDate periodEnd;
}
