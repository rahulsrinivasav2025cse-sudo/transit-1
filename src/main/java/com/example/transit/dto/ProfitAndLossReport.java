package com.example.transit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfitAndLossReport {
    private LocalDate from;
    private LocalDate to;
    private Map<String, BigDecimal> incomeByAccount;
    private Map<String, BigDecimal> expenseByAccount;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netProfit;
}
