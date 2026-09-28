package com.example.transit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetReportItem {
    private Long budgetId;
    private String budgetName;
    private String analyticAccountName;
    private BigDecimal plannedAmount;
    private BigDecimal actualAmount;
    private BigDecimal variance;
    private LocalDate periodStart;
    private LocalDate periodEnd;
}
