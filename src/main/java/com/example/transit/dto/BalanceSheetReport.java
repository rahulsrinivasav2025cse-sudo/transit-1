package com.example.transit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceSheetReport {
    private LocalDate asOfDate;
    private Map<String, BigDecimal> assets;
    private Map<String, BigDecimal> liabilities;
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
}
